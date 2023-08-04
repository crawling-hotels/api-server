package com.example.demo.board.controller;

import com.example.demo.board.dto.BoardDto;
import com.example.demo.board.service.BoardService;
import com.example.demo.calendar.dto.ScheduleDto;
import com.example.demo.common.dto.MessageResponse;
import com.example.demo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @GetMapping("/myPage")
    public ResponseEntity<MessageResponse> viewFromMyPage(@AuthenticationPrincipal User user){
        return ResponseEntity.ok().body(boardService.getBoardsByUser(user));
    }

    @GetMapping("/{hotelName}")
    public ResponseEntity<MessageResponse> viewFromHotel(@PathVariable String hotelName){
        return ResponseEntity.ok().body(boardService.getBoardsByHotelName(hotelName));
    }

    @PostMapping("/{hotelName}")
    public ResponseEntity<Void> create(@AuthenticationPrincipal User user,
                                       @RequestBody BoardDto boardDto,
                                       @PathVariable String hotelName){
        boardService.createBoard(user, boardDto, hotelName);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<Void> update(@AuthenticationPrincipal User user,
                                       @RequestBody BoardDto boardDto,
                                       @PathVariable Long hotelId){
        boardService.updateBoard(user, boardDto, hotelId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal User user,
                                    @PathVariable Long boardId){
        boardService.removeBoard(user, boardId);
        return ResponseEntity.noContent().build();
    }

}
