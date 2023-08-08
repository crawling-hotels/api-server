package com.example.demo.util.exception.handler;

import com.example.demo.image.exception.ImageExtensionNotSupportException;
import com.example.demo.image.exception.ImageFileConvertException;
import com.example.demo.image.exception.ImageFileUploadFailedException;
import com.example.demo.image.exception.ImagePathExtractFailException;
import com.example.demo.board.exception.URLDecodeFailException;
import com.example.demo.calendar.exception.ScheduleNotFoundException;
import com.example.demo.common.dto.MessageResponse;
import com.example.demo.favorite.exception.FavoriteNotFoundException;
import com.example.demo.search.exception.HotelNotFoundException;
import com.example.demo.user.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            ScheduleNotFoundException.class,
            UserNotFoundException.class,
            FavoriteNotFoundException.class,
            HotelNotFoundException.class,
            URLDecodeFailException.class,
            URLDecodeFailException.class,
            ImageExtensionNotSupportException.class,
            ImageFileConvertException.class,
            ImagePathExtractFailException.class,
            ImageFileUploadFailedException.class
    })
    public ResponseEntity<MessageResponse> handleBadRequest(RuntimeException e) {
        MessageResponse message =
                MessageResponse.of(-1, e.getMessage());

        return ResponseEntity.badRequest().body(message);
    }


}