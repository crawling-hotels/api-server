package com.example.demo.calendar.repository;

import com.example.demo.calendar.domain.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, String> {
}
