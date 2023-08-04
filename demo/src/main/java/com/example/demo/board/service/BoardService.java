package com.example.demo.board.service;

import com.example.demo.board.domain.Board;
import com.example.demo.board.dto.BoardDto;
import com.example.demo.board.exception.BoardNotFoundException;
import com.example.demo.board.exception.URLDecodeFailException;
import com.example.demo.board.exception.WriterNotSameException;
import com.example.demo.board.repository.BoardRepository;
import com.example.demo.common.constant.ResponseCodeEnum;
import com.example.demo.common.dto.MessageResponse;
import com.example.demo.hotel.domain.Hotel;
import com.example.demo.hotel.repository.HotelRepository;
import com.example.demo.search.exception.HotelNotFoundException;
import com.example.demo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private HotelRepository hotelRepository;

    public MessageResponse getBoardsByUser(User user){
        var boards = user.getBoards();

        var boardDtos = Optional.ofNullable(boards)
                .orElseGet(() -> new ArrayList<>())
                .stream()
                .map(b -> new BoardDto(b.getId(), b.getTitle(), b.getContent(), b.getImagePath()))
                .collect(Collectors.toList());

        return MessageResponse.of(ResponseCodeEnum.BOARD_SEARCH_SUCCESS, boardDtos);
    }

    public MessageResponse getBoardsByHotelName(String hotelName){
        var hotel = Optional
                .ofNullable(hotelRepository.findByName(hotelName))
                .orElseThrow(() -> new HotelNotFoundException(ResponseCodeEnum.HOTEL_NOT_FOUND.getMessage()))
                .get();

        var boards = hotel.getBoards();

        var boardDtos = Optional.ofNullable(boards)
                .orElseGet(() -> new ArrayList<>())
                .stream()
                .map(b -> new BoardDto(b.getId(), b.getTitle(), b.getContent(), b.getImagePath()))
                .collect(Collectors.toList());

        return MessageResponse.of(ResponseCodeEnum.BOARD_SEARCH_SUCCESS, boardDtos);
    }

    @Transactional
    public void createBoard(User user, BoardDto boardDto, String encoded){
        Hotel hotel = Optional
                .ofNullable(hotelRepository.findByName(decodeURL(encoded)))
                .orElseThrow(() -> new HotelNotFoundException(ResponseCodeEnum.HOTEL_NOT_FOUND.getMessage()))
                .get();

        Board board = new Board(boardDto.getTitle(), boardDto.getContent(), boardDto.getImagePath());
        user.addBoard(board);
        hotel.addBoard(board);

        boardRepository.save(board);
    }

    public void updateBoard(User user, BoardDto boardDto, Long boardId){
        Board board = Optional
                .ofNullable(boardRepository.findById(boardId))
                .orElseThrow(() -> new BoardNotFoundException(ResponseCodeEnum.BOARD_NOT_FOUND.getMessage()))
                .get();

        if(board.getUser() != user){
            throw new WriterNotSameException(ResponseCodeEnum.BOARD_WRITER_NOT_SAME.getMessage());
        }


        board.update(board.getTitle(), boardDto.getContent(), boardDto.getImagePath());
        boardRepository.save(board);
    }

    public void removeBoard(User user, Long boardId){
        Board board = Optional
                .ofNullable(boardRepository.findById(boardId))
                .orElseThrow(() -> new BoardNotFoundException(ResponseCodeEnum.BOARD_NOT_FOUND.getMessage()))
                .get();

        if(board.getUser() != user){
            throw new WriterNotSameException(ResponseCodeEnum.BOARD_WRITER_NOT_SAME.getMessage());
        }

        user.getBoards().remove(board);
        board.getHotel().getBoards().remove(board);

        boardRepository.delete(board);
    }

    private String decodeURL(String input) {
        try {
            return URLDecoder.decode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new URLDecodeFailException(ResponseCodeEnum.URL_DECODE_FAILED.getMessage());
        }
    }
}
