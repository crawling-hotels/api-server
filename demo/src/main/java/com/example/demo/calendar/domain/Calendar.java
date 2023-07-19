package com.example.demo.calendar.domain;

import com.example.demo.user.domain.User;
import jakarta.persistence.*;

import java.util.Collection;

@Entity
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_id")
    private Long id;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.PERSIST)
    private Collection<User> user;

    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL)
    private Collection<Schedule> schedules;
}
