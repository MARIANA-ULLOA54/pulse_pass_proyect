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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class EventSearchIT extends AbstractIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    private Event newEvent(String code, String name, EventCategory category, Venue venue) {
        Event e = new Event();
        e.setEventCode(code);
        e.setName(name);
        e.setCategory(category);
        e.setStatus(EventStatus.PUBLISHED);
        e.setEventDate(LocalDate.now().plusDays(10));
        e.setVenue(venue);
        return e;
    }

    @Test
    @DisplayName("Debe filtrar eventos por categoría y estado")
    void shouldFindEventsByCategoryAndStatus() {
        Venue venue = new Venue();
        venue.setCode("VEN-SRCH-01");
        venue.setName("Teatro Amira");
        venue.setCity("Barranquilla");
        venue.setCapacity(500);
        venue = venueRepository.save(venue);

        eventRepository.save(newEvent("EVT-JAZZ-01", "Festival de Jazz", EventCategory.MUSIC, venue));
        eventRepository.save(newEvent("EVT-TEAT-01", "Obra de Teatro", EventCategory.CULTURE, venue));
        eventRepository.flush();

        List<Event> concerts = eventRepository.findByCategoryAndStatus(EventCategory.MUSIC, EventStatus.PUBLISHED);

        assertThat(concerts).hasSize(1);
        assertThat(concerts.get(0).getName()).isEqualTo("Festival de Jazz");
        assertThat(eventRepository.findByStatus(EventStatus.PUBLISHED)).hasSize(2);
    }
}
