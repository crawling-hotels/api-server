package com.example.demo.favorite.dto;

import com.example.demo.hotel.domain.HotelDetail;
import lombok.Getter;

import java.util.Collection;

@Getter
public class FavoriteHotelDto {
    private Long favoriteId;
    private String name;

    private Collection<HotelDetail> hotelDetails;

    public FavoriteHotelDto(Long favoriteId, String name, Collection<HotelDetail> hotelDetails) {
        this.favoriteId = favoriteId;
        this.name = name;
        this.hotelDetails = hotelDetails;
    }
}
