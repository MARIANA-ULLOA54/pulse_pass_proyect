package com.pulsepass.repository;

import com.pulsepass.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByActiveTrue();
    List<Event> findByVenueId(Long venueId);
}