package com.example.demo.hotel.domain;

import com.example.demo.search.HotelInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 야놀자 Detail, 여기어때 Detail 필요
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HotelDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @Embedded
    private HotelInfo hotelInfo;

    public HotelDetail(HotelInfo hotelInfo) {
        this.hotelInfo = hotelInfo;
    }

    public void setHotel(Hotel hotel){
        this.hotel = hotel;
    }
}
