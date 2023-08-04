package com.example.demo.favorite.service;

import com.example.demo.common.constant.ResponseCodeEnum;
import com.example.demo.common.dto.MessageResponse;
import com.example.demo.favorite.domain.Favorite;
import com.example.demo.favorite.dto.FavoriteHotelDto;
import com.example.demo.favorite.exception.FavoriteNotFoundException;
import com.example.demo.favorite.repository.FavoriteRepository;
import com.example.demo.hotel.domain.Hotel;
import com.example.demo.hotel.repository.HotelRepository;
import com.example.demo.search.exception.HotelNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.exception.UserNotFoundException;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FavoriteService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final HotelRepository hotelRepository;
    @Autowired
    private final FavoriteRepository favoriteRepository;

    public MessageResponse getFavoriteHotels(User user) {
        var favorites = user.getFavorites();

        var favoriteHotelDtos = Optional.ofNullable(favorites)
                .orElseGet(() -> new ArrayList<>())
                .stream()
                .map(f -> new FavoriteHotelDto(f.getId(), f.getHotel().getName(), f.getHotel().getHotelDetails()))
                .collect(Collectors.toList());

        return MessageResponse.of(ResponseCodeEnum.FAVORITE_SEARCH_SUCCESS, favoriteHotelDtos);
    }
    @Transactional
    public void addFavorite(User user, String hotelName) {
        Hotel hotel = Optional
                .ofNullable(hotelRepository.findByName(hotelName))
                .orElseThrow(() -> new HotelNotFoundException(ResponseCodeEnum.HOTEL_NOT_FOUND.getMessage()))
                .get();

        Favorite favorite = new Favorite(user, hotel);
        user.addFavorite(favorite);
        hotel.addFavorite(favorite);

        favoriteRepository.save(favorite);
//        userRepository.save(user);
//        hotelRepository.save(hotel);
    }

    @Transactional
    public void removeFavorite(User user, Long favoriteId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new FavoriteNotFoundException(ResponseCodeEnum.FAVORITE_NOT_FOUND.getMessage()));

        user.getFavorites().remove(favorite);
        favorite.getHotel().getFavorites().remove(favorite);

        favoriteRepository.delete(favorite);

//        userRepository.save(user);
    }
}
