package com.example.demo.util.exception.handler;

import com.example.demo.calendar.exception.ScheduleNotFoundException;
import com.example.demo.common.constant.ResponseCodeEnum;
import com.example.demo.common.dto.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ScheduleNotFoundException.class})
    public ResponseEntity<MessageResponse> handleBadRequest(Exception e) {
        MessageResponse message = MessageResponse.of(ResponseCodeEnum.SCHEDULE_NOT_FOUND.getCode() ,
                e.getMessage());

        return ResponseEntity.badRequest().body(message);
    }
}
