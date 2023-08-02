package com.example.demo.user.controller;

import com.example.demo.common.constant.ResponseCodeEnum;
import com.example.demo.common.dto.MessageResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    @Autowired
    private final UserService userService;

    @GetMapping("/myPage")
    public ResponseEntity<MessageResponse> myPage(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok().body(MessageResponse.of(ResponseCodeEnum.SIMPLE_REQUEST_FAILURE, userDetails.getUsername()));
    }
}
