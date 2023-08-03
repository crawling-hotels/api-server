package com.example.demo.favorite.service;

import com.example.demo.common.constant.ResponseCodeEnum;
import com.example.demo.common.dto.MessageResponse;
import com.example.demo.favorite.domain.Favorite;
import com.example.demo.favorite.dto.FavoriteHotelDto;
import com.example.demo.favorite.repository.FavoriteRepository;
import com.example.demo.hotel.domain.Hotel;
import com.example.demo.hotel.repository.HotelRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.exception.UserNotFoundException;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
//        String username = userDetails.getUsername();
//
//        User user = (User) AuthService.USERS.get(username);

        var favorites = user.getFavorites();

        var favoriteHotelDtos = Optional.ofNullable(favorites)
                .orElseGet(() -> new ArrayList<>())
                .stream()
                .map(f -> new FavoriteHotelDto(f.getId(), f.getHotel().getName(), f.getHotel().getHotelDetails()))
                .collect(Collectors.toList());

        return MessageResponse.of(ResponseCodeEnum.FAVORITE_SEARCH_SUCCESS, favoriteHotelDtos);
    }
    public void addFavorite(User user, String hotelName) {
//        String username = userDetails.getUsername();
//
//        User user = (User) AuthService.USERS.get(username);
        Hotel hotel = Optional
                .ofNullable(hotelRepository.findByName(hotelName))
                .orElseThrow(() -> new UserNotFoundException(ResponseCodeEnum.USER_NOT_FOUND.getMessage()))
                .get();

        Favorite favorite = new Favorite(user, hotel);
        favoriteRepository.save(favorite);
    }
    public void removeFavorite(User user, Long favoriteId) {
//        String username = userDetails.getUsername();
        Favorite favorite = Optional
                .ofNullable(favoriteRepository.findById(favoriteId))
                .orElseThrow(() -> new UserNotFoundException(ResponseCodeEnum.USER_NOT_FOUND.getMessage()))
                .get();

//        User user = (User) AuthService.USERS.get(username);

        user.removeFavorite(favorite);
        userRepository.save(user);
    }
}
