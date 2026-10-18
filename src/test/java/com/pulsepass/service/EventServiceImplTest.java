package com.pulsepass.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pulsepass.domain.Artist;
import com.pulsepass.domain.Event;
import com.pulsepass.domain.Venue;
import com.pulsepass.domain.enums.EventCategory;
import com.pulsepass.domain.enums.EventStatus;
import com.pulsepass.dto.request.CreateEventRequest;
import com.pulsepass.exception.BusinessRuleException;
import com.pulsepass.exception.DuplicateResourceException;
import com.pulsepass.exception.ResourceNotFoundException;
import com.pulsepass.mapper.EventMapper;
import com.pulsepass.repository.ArtistRepository;
import com.pulsepass.repository.EventRepository;
import com.pulsepass.repository.VenueRepository;
import com.pulsepass.service.impl.EventServiceImpl;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock EventRepository eventRepository;
    @Mock VenueRepository venueRepository;
    @Mock ArtistRepository artistRepository;
    @Mock EventMapper eventMapper;
    @Mock AuthorizationService authorizationService;
    @InjectMocks EventServiceImpl eventService;

    Event draft;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        draft = new Event();
        draft.setEventCode("CMF-2026");
        draft.setStatus(EventStatus.DRAFT);
    }

    private CreateEventRequest request(LocalDate date) {
        return new CreateEventRequest("CMF-2026", "Caribbean Music Fest", "desc",
                EventCategory.MUSIC, date, 18, "VEN-SMR-01");
    }

    @Test
    void create_valid_savesAsDraft() {
        Venue venue = new Venue();
        when(eventRepository.findByEventCode("CMF-2026")).thenReturn(Optional.empty());
        when(venueRepository.findByCode("VEN-SMR-01")).thenReturn(Optional.of(venue));
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArgument(0));

        eventService.create(request(LocalDate.now().plusDays(30)));

        ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(EventStatus.DRAFT);
        assertThat(captor.getValue().getVenue()).isSameAs(venue);
    }

    @Test
    void create_duplicateCode_throws() {
        when(eventRepository.findByEventCode("CMF-2026")).thenReturn(Optional.of(draft));
        assertThatThrownBy(() -> eventService.create(request(LocalDate.now().plusDays(30))))
                .isInstanceOf(DuplicateResourceException.class);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void create_pastDate_throws() {
        when(eventRepository.findByEventCode("CMF-2026")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> eventService.create(request(LocalDate.now().minusDays(1))))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void create_unknownVenue_throws() {
        when(eventRepository.findByEventCode("CMF-2026")).thenReturn(Optional.empty());
        when(venueRepository.findByCode("VEN-SMR-01")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> eventService.create(request(LocalDate.now().plusDays(30))))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByCode_draftAsAnonymous_isHidden() {
        when(eventRepository.findByEventCode("CMF-2026")).thenReturn(Optional.of(draft));
        when(authorizationService.isStaffOrAdmin()).thenReturn(false);
        assertThatThrownBy(() -> eventService.findByCode("CMF-2026"))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(eventMapper, never()).toResponse(any());
    }

    @Test
    void findByCode_draftAsStaff_isVisible() {
        when(eventRepository.findByEventCode("CMF-2026")).thenReturn(Optional.of(draft));
        when(authorizationService.isStaffOrAdmin()).thenReturn(true);
        eventService.findByCode("CMF-2026");
        verify(eventMapper).toResponse(draft);
    }

    @Test
    void findByCode_published_isPublic() {
        draft.setStatus(EventStatus.PUBLISHED);
        when(eventRepository.findByEventCode("CMF-2026")).thenReturn(Optional.of(draft));
        eventService.findByCode("CMF-2026");
        verify(eventMapper).toResponse(draft);
    }

    @Test
    void findByArtist_anonymous_filtersDrafts() {
        Event published = new Event();
        published.setStatus(EventStatus.PUBLISHED);
        when(authorizationService.isStaffOrAdmin()).thenReturn(false);
        when(eventRepository.findByArtistsStageName("Solar Beat")).thenReturn(List.of(published, draft));

        eventService.findByArtist("Solar Beat");

        verify(eventMapper).toSummaryList(argThat(list -> list.size() == 1 && list.get(0) == published));
    }

    @Test
    void publish_draft_becomesPublished() {
        when(eventRepository.findByEventCode("CMF-2026")).thenReturn(Optional.of(draft));
        eventService.publish("CMF-2026");
        assertThat(draft.getStatus()).isEqualTo(EventStatus.PUBLISHED);
    }

    @Test
    void publish_alreadyPublished_throws() {
        draft.setStatus(EventStatus.PUBLISHED);
        when(eventRepository.findByEventCode("CMF-2026")).thenReturn(Optional.of(draft));
        assertThatThrownBy(() -> eventService.publish("CMF-2026"))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void addArtist_valid_linksArtist() {
        Artist artist = new Artist();
        when(eventRepository.findByEventCode("CMF-2026")).thenReturn(Optional.of(draft));
        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        eventService.addArtist("CMF-2026", 1L);
        assertThat(draft.getArtists()).containsExactly(artist);
    }

    @Test
    void addArtist_repeated_throws() {
        Artist artist = new Artist();
        draft.addArtist(artist);
        when(eventRepository.findByEventCode("CMF-2026")).thenReturn(Optional.of(draft));
        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        assertThatThrownBy(() -> eventService.addArtist("CMF-2026", 1L))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void addArtist_unknownArtist_throws() {
        when(eventRepository.findByEventCode("CMF-2026")).thenReturn(Optional.of(draft));
        when(artistRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> eventService.addArtist("CMF-2026", 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addArtist_cancelledEvent_throws() {
        draft.setStatus(EventStatus.CANCELLED);
        when(eventRepository.findByEventCode("CMF-2026")).thenReturn(Optional.of(draft));
        assertThatThrownBy(() -> eventService.addArtist("CMF-2026", 1L))
                .isInstanceOf(BusinessRuleException.class);
    }
}
