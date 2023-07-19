package com.example.demo.hotel.domain;

import com.example.demo.board.domain.Board;
import com.example.demo.calendar.domain.Schedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "board")
    private Collection<Board> boards;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "schedule")
    private Collection<Schedule> schedules;


    public Hotel(String name) {
        this.name = name;
    }
}
