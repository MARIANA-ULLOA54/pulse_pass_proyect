package com.pulsepass;

import com.pulsepass.domain.Artist;
import com.pulsepass.domain.Event;
import com.pulsepass.domain.enums.EventCategory;
import com.pulsepass.domain.enums.EventStatus;
import com.pulsepass.repository.ArtistRepository;
import com.pulsepass.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class EventArtistIT extends AbstractIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Test
    @DisplayName("Debe vincular múltiples artistas a un evento correctamente")
    void shouldLinkArtistsToEvent() {
        Artist artist1 = new Artist();
        artist1.setName("Shakira");
        artist1.setGenre("Pop");

        Artist artist2 = new Artist();
        artist2.setName("Carlos Vives");
        artist2.setGenre("Vallenato Pop");

        Artist savedArtist1 = artistRepository.save(artist1);
        Artist savedArtist2 = artistRepository.save(artist2);

        Event event = new Event();
        event.setTitle("Festival de Música Caribe");
        event.setCategory(EventCategory.CONCERT);
        event.setStatus(EventStatus.ACTIVE);
        event.setEventDate(LocalDateTime.now().plusDays(20));
        event.setArtists(Set.of(savedArtist1, savedArtist2));

        Event savedEvent = eventRepository.save(event);

        assertThat(savedEvent.getId()).isNotNull();
        assertThat(savedEvent.getArtists()).hasSize(2);
        assertThat(savedEvent.getArtists())
                .extracting(Artist::getName)
                .containsExactlyInAnyOrder("Shakira", "Carlos Vives");
    }
}