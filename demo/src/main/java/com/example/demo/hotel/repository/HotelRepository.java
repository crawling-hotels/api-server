package com.example.demo.hotel.repository;

import com.example.demo.hotel.domain.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    boolean existsByName(String name);
    Optional<Hotel> findByName(String name);
}
