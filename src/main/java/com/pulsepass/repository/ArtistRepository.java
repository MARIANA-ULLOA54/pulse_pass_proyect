package com.pulsepass.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulsepass.domain.Artist;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    // FR-ART-002
    Optional<Artist> findByStageName(String stageName);
}