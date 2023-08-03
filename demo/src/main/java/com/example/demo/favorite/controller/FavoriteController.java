package com.example.demo.favorite.controller;

import com.example.demo.common.dto.MessageResponse;
import com.example.demo.favorite.service.FavoriteService;
import com.example.demo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite")
public class FavoriteController {
    @Autowired
    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<MessageResponse> view(@AuthenticationPrincipal User user){
        return ResponseEntity.ok().body(favoriteService.getFavoriteHotels(user));
    }

    @PostMapping
    public ResponseEntity<Void> add(@AuthenticationPrincipal User user, @RequestBody String hotelName){
        favoriteService.addFavorite(user, hotelName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal User user, Long favoriteId){
        favoriteService.removeFavorite(user, favoriteId);
        return ResponseEntity.noContent().build();
    }
}
