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

import java.time.LocalDate;
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
        venue.setCode("VEN-TEST-01");
        venue.setName("Estadio Metropolitano");
        venue.setCity("Barranquilla");
        venue.setCapacity(50000);
        Venue savedVenue = venueRepository.save(venue);

        Event event = new Event();
        event.setEventCode("EVT-ROCK-01");
        event.setName("Concierto de Rock");
        event.setCategory(EventCategory.MUSIC);
        event.setStatus(EventStatus.DRAFT);
        event.setEventDate(LocalDate.now().plusDays(30));
        event.setVenue(savedVenue);

        Event savedEvent = eventRepository.saveAndFlush(event);

        assertThat(savedEvent.getId()).isNotNull();
        Optional<Event> found = eventRepository.findByEventCode("EVT-ROCK-01");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Concierto de Rock");
        assertThat(found.get().getVenue().getName()).isEqualTo("Estadio Metropolitano");
    }
}
