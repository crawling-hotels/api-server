package com.example.demo.board.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.demo.board.domain.Board;
import com.example.demo.board.dto.BoardDto;
import com.example.demo.board.exception.*;
import com.example.demo.board.repository.BoardRepository;
import com.example.demo.common.constant.ResponseCodeEnum;
import com.example.demo.common.dto.MessageResponse;
import com.example.demo.hotel.domain.Hotel;
import com.example.demo.hotel.repository.HotelRepository;
import com.example.demo.search.exception.HotelNotFoundException;
import com.example.demo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
    private BoardRepository boardRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final static String objectFolder = "review";

    @Autowired
    private AmazonS3 s3Client;

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
        if (!isValidImageExtension(multipartFile.getOriginalFilename())) {
            throw new ImageExtensionNotSupportException(ResponseCodeEnum.IMAGE_EXTENSION_NOT_SUPPORT.getMessage());
        }

        String encodedFileName = encodeURL(multipartFile.getOriginalFilename());

        File file = convertMultipartFileToFile(multipartFile);

        s3Client.putObject(new PutObjectRequest(bucketName, objectFolder + "/" + encodedFileName, file));

        file.delete();

        Hotel hotel = hotelRepository.findByName(decodeURL(encoded))
                .orElseThrow(() -> new HotelNotFoundException(ResponseCodeEnum.HOTEL_NOT_FOUND.getMessage()));

        Board board = new Board(
                boardDto.getTitle(),
                boardDto.getContent(),
                "https://" + bucketName + ".s3.amazonaws.com/" + objectFolder + "/" + encodedFileName,
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

        if(board.getUser() != user){
            throw new WriterNotSameException(ResponseCodeEnum.BOARD_WRITER_NOT_SAME.getMessage());
        }

        if (multipartFile != null && !multipartFile.isEmpty()) {
            if(boardDto.getImagePath() != null){
                String objectKey = extractObjectKeyFromImagePath(board.getImagePath());
                DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, objectFolder + "/" + objectKey);
                s3Client.deleteObject(deleteObjectRequest);
            }

            if (!isValidImageExtension(multipartFile.getOriginalFilename())) {
                throw new ImageExtensionNotSupportException(ResponseCodeEnum.IMAGE_EXTENSION_NOT_SUPPORT.getMessage());
            }

            String encodedFileName = encodeURL(multipartFile.getOriginalFilename());

            File file = convertMultipartFileToFile(multipartFile);

            s3Client.putObject(new PutObjectRequest(bucketName, "review/" + encodedFileName, file));

            file.delete();
        }

        board.update(board.getTitle(), boardDto.getContent(), boardDto.getImagePath(),
                boardDto.getOverallRating(), boardDto.getHygieneScore(), boardDto.getLocationScore(), boardDto.getAmenitiesScore());
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
            String objectKey = extractObjectKeyFromImagePath(board.getImagePath());
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, objectFolder + "/" + objectKey);
            s3Client.deleteObject(deleteObjectRequest);
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

    private String encodeURL(String input){
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new URLEncodeFailException(ResponseCodeEnum.URL_ENCODE_FAILED.getMessage());
        }
    }

    private boolean isValidImageExtension(String filename) {
        String[] allowedExtensions = { "jpg", "jpeg", "png" };
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return Arrays.asList(allowedExtensions).contains(extension);
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile){
        try {
            File file = new File(multipartFile.getOriginalFilename());
            multipartFile.transferTo(file);

            return file;
        }catch (IOException e) {
            throw new ImageFileConvertException(ResponseCodeEnum.IMAGE_CONVERT_FAILED.getMessage());
        }
    }

    private String extractObjectKeyFromImagePath(String imagePath){
        Pattern pattern = Pattern.compile("/review/(.*)");
        Matcher matcher = pattern.matcher(imagePath);

        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new ImagePathExtractFailException(ResponseCodeEnum.IMAGE_PATH_EXTRACT_FAILED.getMessage());
    }

}