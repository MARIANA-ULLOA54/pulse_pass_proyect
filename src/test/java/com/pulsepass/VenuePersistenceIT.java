package com.pulsepass;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.pulsepass.domain.Venue;
import com.pulsepass.repository.VenueRepository;

@Transactional
class VenuePersistenceIT extends AbstractIntegrationTest {

    @Autowired
    private VenueRepository venueRepository;

    @Test
    @DisplayName("Debe guardar un venue y encontrarlo por código y como activo")
    void shouldSaveAndFindVenue() {
        Venue venue = new Venue();
        venue.setCode("VEN-SMR-01");
        venue.setName("Marina Convention Center");
        venue.setCity("Santa Marta");
        venue.setCapacity(3);
        venueRepository.saveAndFlush(venue);

        assertThat(venueRepository.findByCode("VEN-SMR-01")).isPresent();
        assertThat(venueRepository.findByActiveTrue())
                .extracting(Venue::getCode)
                .contains("VEN-SMR-01");
    }
}
