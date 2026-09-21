package com.pulsepass;

import com.pulsepass.domain.Event;
import com.pulsepass.domain.Venue;
import com.pulsepass.domain.enums.EventCategory;
import com.pulsepass.domain.enums.EventStatus;
import com.pulsepass.repository.EventRepository;
import com.pulsepass.repository.VenueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class EventRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Test
    @DisplayName("Debe guardar y recuperar un evento con su venue asociado")
    void shouldSaveAndFindEvent() {
        Venue venue = new Venue();
        venue.setName("Estadio Metropolitano");
        venue.setCity("Barranquilla");
        venue.setCapacity(50000);
        Venue savedVenue = venueRepository.save(venue);

        Event event = new Event();
        event.setTitle("Concierto de Rock");
        event.setCategory(EventCategory.CONCERT);
        event.setStatus(EventStatus.ACTIVE);
        event.setEventDate(LocalDateTime.now().plusDays(30));
        event.setVenue(savedVenue);

        Event savedEvent = eventRepository.save(event);

        assertThat(savedEvent.getId()).isNotNull();
        
        Optional<Event> foundEvent = eventRepository.findById(savedEvent.getId());
        assertThat(foundEvent).isPresent();
        assertThat(foundEvent.get().getTitle()).isEqualTo("Concierto de Rock");
        assertThat(foundEvent.get().getVenue().getName()).isEqualTo("Estadio Metropolitano");
    }
}