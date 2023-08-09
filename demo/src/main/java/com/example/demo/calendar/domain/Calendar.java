package com.example.demo.calendar.domain;

import com.example.demo.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_id")
    private Long id;

    @OneToMany(mappedBy = "calendar", cascade = {CascadeType.PERSIST})
    private Collection<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "calendar", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Collection<Schedule> schedules = new ArrayList<>();

    public void addSchedule(Schedule schedule){
        this.schedules.add(schedule);
    }

    public void addUser(User invitee){
        invitee.setCalendar(this);
        this.users.add(invitee);
    }
}
