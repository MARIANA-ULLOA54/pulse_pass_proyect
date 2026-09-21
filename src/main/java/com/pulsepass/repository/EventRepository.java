package com.pulsepass.repository;

import com.pulsepass.domain.Event;
import com.pulsepass.domain.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    // FR-EVT-001/002 / AC-002
    Optional<Event> findByEventCode(String eventCode);

    // FR-EVT-005 / AC-006
    List<Event> findByStatusOrderByEventDateAsc(EventStatus status);

    // FR-VEN-004
    List<Event> findByVenue_CodeOrderByEventDateAsc(String venueCode);

    // FR-SRC-001 / FR-ART-004 / AC-007 — JPQL con JOIN, sin duplicados
    @Query("""
           SELECT DISTINCT e FROM Event e
           JOIN e.artists a
           WHERE a.stageName = :stageName
           ORDER BY e.eventDate ASC
           """)
    List<Event> findByArtistStageName(@Param("stageName") String stageName);

    // FR-SRC-002 — filtra por venue.city y artist.stageName
    @Query("""
           SELECT DISTINCT e FROM Event e
           JOIN e.artists a
           WHERE e.venue.city = :city
             AND a.stageName = :stageName
           ORDER BY e.eventDate ASC
           """)
    List<Event> findByCityAndArtist(@Param("city") String city,
                                     @Param("stageName") String stageName);

    // FR-SRC-003 — eventos publicados, posteriores a fecha, en ciudad, artista case-insensitive
    @Query("""
           SELECT DISTINCT e FROM Event e
           JOIN e.artists a
           WHERE e.status = 'PUBLISHED'
             AND e.eventDate > :afterDate
             AND e.venue.city = :city
             AND LOWER(a.stageName) LIKE LOWER(CONCAT('%', :artistNameFragment, '%'))
           ORDER BY e.eventDate ASC
           """)
    List<Event> findRecommendedEvents(@Param("afterDate") LocalDate afterDate,
                                       @Param("city") String city,
                                       @Param("artistNameFragment") String artistNameFragment);
}
