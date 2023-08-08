package com.example.demo.board.service;

import com.example.demo.board.domain.Board;
import com.example.demo.board.dto.BoardDto;
import com.example.demo.board.exception.*;
import com.example.demo.board.repository.BoardRepository;
import com.example.demo.common.constant.ResponseCodeEnum;
import com.example.demo.common.dto.MessageResponse;
import com.example.demo.hotel.domain.Hotel;
import com.example.demo.hotel.repository.HotelRepository;
import com.example.demo.image.exception.ImageExtensionNotSupportException;
import com.example.demo.image.exception.ImageFileConvertException;
import com.example.demo.image.exception.ImagePathExtractFailException;
import com.example.demo.image.service.S3ImageService;
import com.example.demo.search.exception.HotelNotFoundException;
import com.example.demo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    @Autowired
    private S3ImageService s3ImageService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Transactional(readOnly = true)
    public MessageResponse getBoardsByUser(User user){
        var boards = user.getBoards();

        var boardDtos = Optional.ofNullable(boards)
                .orElseGet(() -> new ArrayList<>())
                .stream()
                .map(b -> new BoardDto(b.getId(), b.getTitle(), b.getContent(), b.getImagePath(),
                    b.getOverallRating(), b.getHygieneScore(), b.getLocationScore(), b.getAmenitiesScore()))
                .collect(Collectors.toList());

        return MessageResponse.of(ResponseCodeEnum.BOARD_SEARCH_SUCCESS, boardDtos);
    }

    @Transactional(readOnly = true)
    public MessageResponse getBoardsByHotelName(String encoded){
        var hotel = hotelRepository.findByName(decodeURL(encoded))
                .orElseThrow(() -> new HotelNotFoundException(ResponseCodeEnum.HOTEL_NOT_FOUND.getMessage()));

        var boards = hotel.getBoards();

        var boardDtos = Optional.ofNullable(boards)
                .orElseGet(() -> new ArrayList<>())
                .stream()
                .map(b -> new BoardDto(b.getId(), b.getTitle(), b.getContent(), b.getImagePath(),
                        b.getOverallRating(), b.getHygieneScore(), b.getLocationScore(), b.getAmenitiesScore()))
                .collect(Collectors.toList());

        return MessageResponse.of(ResponseCodeEnum.BOARD_SEARCH_SUCCESS, boardDtos);
    }

    @Transactional
    public void createBoard(User user, BoardDto boardDto, String encoded, MultipartFile multipartFile){
        String imagePath = s3ImageService.uploadFileV1(multipartFile);

        Hotel hotel = hotelRepository.findByName(decodeURL(encoded))
                .orElseThrow(() -> new HotelNotFoundException(ResponseCodeEnum.HOTEL_NOT_FOUND.getMessage()));

        Board board = new Board(
                boardDto.getTitle(),
                boardDto.getContent(),
                imagePath,
                boardDto.getOverallRating(),
                boardDto.getHygieneScore(),
                boardDto.getLocationScore(),
                boardDto.getAmenitiesScore()
        );
        user.addBoard(board);
        hotel.addBoard(board);

        boardRepository.save(board);
    }

    @Transactional
    public void updateBoard(User user, BoardDto boardDto, Long boardId, @Nullable MultipartFile multipartFile){
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException(ResponseCodeEnum.BOARD_NOT_FOUND.getMessage()));

        String imagePath = boardDto.getImagePath();

        if(board.getUser() != user){
            throw new WriterNotSameException(ResponseCodeEnum.BOARD_WRITER_NOT_SAME.getMessage());
        }

        if (multipartFile != null && !multipartFile.isEmpty()) {
            if(boardDto.getImagePath() != null){
                s3ImageService.deleteFileV1(imagePath);
            }

            imagePath = s3ImageService.uploadFileV1(multipartFile);
        }

        board.update(
                board.getTitle(),
                boardDto.getContent(),
                imagePath,
                boardDto.getOverallRating(),
                boardDto.getHygieneScore(),
                boardDto.getLocationScore(),
                boardDto.getAmenitiesScore()
        );
        boardRepository.save(board);
    }

    @Transactional
    public void removeBoard(User user, Long boardId){
        Board board = Optional
                .ofNullable(boardRepository.findById(boardId))
                .orElseThrow(() -> new BoardNotFoundException(ResponseCodeEnum.BOARD_NOT_FOUND.getMessage()))
                .get();

        if(board.getUser() != user){
            throw new WriterNotSameException(ResponseCodeEnum.BOARD_WRITER_NOT_SAME.getMessage());
        }

        if(board.getImagePath() != null){
            s3ImageService.deleteFileV1(board.getImagePath());
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