package com.example.demo.hotel.domain;

import com.example.demo.board.domain.Board;
import com.example.demo.calendar.domain.Schedule;
import com.example.demo.favorite.domain.Favorite;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "hotel", orphanRemoval = true)
    private List<Favorite> favorites;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "hotel")
    private Collection<HotelDetail> hotelDetails;

    public Hotel(String name) {
        this.name = name;
    }

    public void addFavorite(Favorite favorite){
        if(favorites == null){
            favorites = new ArrayList<>();
        }

        this.favorites.add(favorite);
        favorite.setHotel(this);
    }

    public void addBoard(Board board){
        if(boards == null){
            boards = new ArrayList<>();
        }

        this.boards.add(board);
    }

    public void addHotelDetailAll(Collection<HotelDetail> hotelDetails){

        this.hotelDetails = hotelDetails;
        for(HotelDetail hd: hotelDetails){
            hd.setHotel(this);
        }
    }


}
