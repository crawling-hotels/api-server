package com.example.demo.hotel.domain;

import com.example.demo.search.HotelInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 야놀자 Detail, 여기어때 Detail 필요
 */
@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HotelDetail extends HotelInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    public HotelDetail(String type, String path, String image, String score) {
        super(type, path, image, score);
    }

}
