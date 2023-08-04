package com.example.demo.util.exception.handler;

import com.example.demo.calendar.exception.ScheduleNotFoundException;
import com.example.demo.common.constant.ResponseCodeEnum;
import com.example.demo.common.dto.MessageResponse;
import com.example.demo.favorite.exception.FavoriteNotFoundException;
import com.example.demo.search.exception.HotelNotFoundException;
import com.example.demo.user.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ScheduleNotFoundException.class, UserNotFoundException.class, FavoriteNotFoundException.class, HotelNotFoundException.class})
    public ResponseEntity<MessageResponse> handleBadRequest(Exception e) {
        MessageResponse message =
                MessageResponse.of(ResponseCodeEnum.SIMPLE_REQUEST_FAILURE.getCode(), e.getMessage());

        return ResponseEntity.badRequest().body(message);
    }
}