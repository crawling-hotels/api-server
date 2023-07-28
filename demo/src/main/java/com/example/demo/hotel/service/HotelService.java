package com.example.demo.hotel.service;

import com.example.demo.hotel.domain.Hotel;
import com.example.demo.hotel.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;

    public Hotel getHotel(String name){
        Optional<Hotel> hotel = hotelRepository.findByName(name);

        if(hotel.isPresent()){
            return hotel.get();
        }else{
            Hotel newHotel = setHotel(name);
            return newHotel;
        }
    }

    private Hotel setHotel(String name){
        Hotel newHotel = new Hotel(name);
        hotelRepository.save(newHotel);
        return newHotel;
    }
}
