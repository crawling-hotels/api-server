package com.example.demo.calendar.domain;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_id")
    private Long id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private Collection<User> user;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private Collection<Schedule> schedules;
}
