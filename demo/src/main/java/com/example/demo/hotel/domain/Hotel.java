package com.example.demo.hotel.domain;

import com.example.demo.board.domain.Board;
import com.example.demo.calendar.domain.Schedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(indexes = @Index(columnList = "name", name = "idx_hotel_01"))
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "hotel")
    private Collection<Board> boards;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "hotel")
    private Collection<Schedule> schedules;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "hotel")
    private Collection<HotelDetail> hotelDetails;

    public Hotel(String name) {
        this.name = name;
    }

    public void addHotelDetailAll(Collection<HotelDetail> hotelDetails){
        this.hotelDetails = hotelDetails;
    }
}
