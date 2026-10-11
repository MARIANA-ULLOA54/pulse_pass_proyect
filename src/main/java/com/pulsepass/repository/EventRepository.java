package com.pulsepass.repository;

import com.pulsepass.domain.Event;
import com.pulsepass.domain.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByActiveTrue();
    List<Event> findByVenueId(Long venueId);
    Optional<Event> findByEventCode(String eventCode);
    List<Event> findByStatus(EventStatus status);
    List<Event> findByArtistsStageName(String stageName);
}
