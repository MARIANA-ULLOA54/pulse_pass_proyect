package com.pulsepass;

import com.pulsepass.domain.Event;
import com.pulsepass.domain.enums.EventCategory;
import com.pulsepass.domain.enums.EventStatus;
import com.pulsepass.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class EventSearchIT extends AbstractIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("Debe filtrar eventos por categoría y estado activo")
    void shouldFindEventsByCategoryAndStatus() {
        Event event1 = new Event();
        event1.setTitle("Festival de Jazz");
        event1.setCategory(EventCategory.CONCERT);
        event1.setStatus(EventStatus.ACTIVE);
        event1.setEventDate(LocalDateTime.now().plusDays(10));
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setTitle("Obra de Teatro");
        event2.setCategory(EventCategory.THEATER);
        event2.setStatus(EventStatus.ACTIVE);
        event2.setEventDate(LocalDateTime.now().plusDays(12));
        eventRepository.save(event2);

        List<Event> concerts = eventRepository.findByCategoryAndStatus(EventCategory.CONCERT, EventStatus.ACTIVE);

        assertThat(concerts).hasSize(1);
        assertThat(concerts.get(0).getTitle()).isEqualTo("Festival de Jazz");
    }
}