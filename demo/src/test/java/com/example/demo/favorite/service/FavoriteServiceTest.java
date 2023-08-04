package com.example.demo.favorite.service;

import com.example.demo.common.constant.ResponseCodeEnum;
import com.example.demo.common.dto.MessageResponse;
import com.example.demo.favorite.domain.Favorite;
import com.example.demo.favorite.repository.FavoriteRepository;
import com.example.demo.hotel.domain.Hotel;
import com.example.demo.hotel.repository.HotelRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@SpringBootTest
@Transactional
public class FavoriteServiceTest {
    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("찜하기 조회 성공 - 존재하지 않은 스케줄")
    public void testGetFavoriteHotels() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = User.builder()
                .username("user2")
                .password(passwordEncoder.encode("user2Pass"))
                .role("USER")
                .build();

        Hotel hotel = new Hotel("강릉 호텔");

        Favorite favorite = new Favorite(user, hotel);
        favoriteRepository.save(favorite);

        MessageResponse messageResponse = favoriteService.getFavoriteHotels(user);

        assertEquals(ResponseCodeEnum.FAVORITE_SEARCH_SUCCESS, messageResponse.getCode());
        assertEquals(1, messageResponse.getData());
    }

    @Test
    public void testAddFavorite() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = User.builder()
                .username("user2")
                .password(passwordEncoder.encode("user2Pass"))
                .role("USER")
                .build();
        userRepository.save(user);

        Hotel hotel = new Hotel("강릉 호텔");
        hotelRepository.save(hotel);

        favoriteService.addFavorite(user, hotel.getName());

        assertEquals(1, favoriteRepository.count());
    }

    @Test
    @Transactional
    public void testRemoveFavorite() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


        User user = User.builder()
                .username("user2")
                .password(passwordEncoder.encode("user2Pass"))
                .role("USER")
                .build();
        userRepository.save(user);

        Hotel hotel = new Hotel("강릉 호텔");
        hotelRepository.save(hotel);

        assertEquals(1, user.getId());
        assertEquals(1, hotel.getId());

        favoriteService.addFavorite(user, hotel.getName());

//        Favorite favorite = favoriteRepository.findById(1L).get();
//        assertEquals(1, favorite.getId());

        assertEquals(user, user.getFavorites().stream().findFirst().get().getUser());
        assertEquals(1, user.getFavorites().size());
        assertEquals(1, user.getFavorites().stream().findFirst().get().getId());
        assertEquals("user2", user.getFavorites().stream().findFirst().get().getUser().getUsername());
        assertEquals("강릉 호텔", user.getFavorites().stream().findFirst().get().getHotel().getName());
        assertEquals("강릉 호텔", hotel.getName());

        //addFavorite에서 name을 통해 다시 호출하니까 같은 디비지만 객체 메모리 주소가 달라서 그런게 아닐까??
        Hotel hotel2 = hotelRepository.findById(1L).get();
        assertEquals(1, hotel2.getFavorites().size());
        assertEquals(1, hotel.getFavorites().size());
//        assertEquals(hotel, user.getFavorites().stream().findFirst().get().getHotel());


        assertEquals(1, favoriteRepository.count());

        favoriteService.removeFavorite(user, user.getFavorites().stream().findFirst().get().getId());


        // no value present
//        assertEquals(user.getId(), favoriteRepository.findById(1L).get());

        assertFalse(favoriteRepository.existsById(1L));
        assertEquals(0, favoriteRepository.count());
//
        assertEquals(0, user.getFavorites().size());
        assertEquals(0, hotel.getFavorites().size());
        assertEquals(0, hotel2.getFavorites().size());


    }
}
