package com.pulsepass;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.pulsepass.domain.Artist;
import com.pulsepass.domain.Event;
import com.pulsepass.domain.Venue;
import com.pulsepass.domain.enums.EventCategory;
import com.pulsepass.domain.enums.EventStatus;
import com.pulsepass.repository.ArtistRepository;
import com.pulsepass.repository.EventRepository;
import com.pulsepass.repository.VenueRepository;

@Transactional
class VenuePersistenceIT extends AbstractIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Test
    @DisplayName("Debe vincular múltiples artistas a un evento correctamente")
    void shouldLinkArtistsToEvent() {
        Artist artist1 = new Artist();
        artist1.setStageName("Shakira");
        artist1.setGenre("Pop");
        Artist artist2 = new Artist();
        artist2.setStageName("Carlos Vives");
        artist2.setGenre("Vallenato Pop");
        artist1 = artistRepository.save(artist1);
        artist2 = artistRepository.save(artist2);

        Venue venue = new Venue();
        venue.setCode("VEN-ART-01");
        venue.setName("Plaza Mayor");
        venue.setCity("Santa Marta");
        venue.setCapacity(1000);
        venue = venueRepository.save(venue);

        Event event = new Event();
        event.setEventCode("EVT-ART-01");
        event.setName("Festival de Música Caribe");
        event.setCategory(EventCategory.MUSIC);
        event.setStatus(EventStatus.DRAFT);
        event.setEventDate(LocalDate.now().plusDays(20));
        event.setVenue(venue);
        event.addArtist(artist1);
        event.addArtist(artist2);

        Event saved = eventRepository.saveAndFlush(event);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getArtists())
                .extracting(artist -> artist.getStageName())
                .containsExactlyInAnyOrder("Shakira", "Carlos Vives");
        assertThat(eventRepository.findByArtistsStageName("Shakira")).hasSize(1);
    }
}
