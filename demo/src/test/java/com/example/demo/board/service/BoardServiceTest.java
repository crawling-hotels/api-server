package com.example.demo.board.service;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.example.demo.board.dto.BoardDto;
import com.example.demo.board.repository.BoardRepository;
import com.example.demo.hotel.domain.Hotel;
import com.example.demo.hotel.repository.HotelRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

public class BoardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private BoardService boardService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateBoard() throws Exception {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = User.builder()
                .username("user2")
                .password(passwordEncoder.encode("user2Pass"))
                .role("USER")
                .build();
        String encoded = "hotel_encoded"; // Set up encoded string
        MultipartFile multipartFile = mock(MultipartFile.class);

        when(multipartFile.getOriginalFilename()).thenReturn("image.jpg");

        Hotel hotel = new Hotel("강릉 호텔"); // Set up hotel object
        when(hotelRepository.findByName(any())).thenReturn(java.util.Optional.of(hotel));

        // Mock S3 client behavior
        when(s3Client.putObject(any(), any(), any(File.class))).thenReturn(mock(PutObjectResult.class));

        BoardDto boardDto = BoardDto.builder()
                .title("Test Board")
                .content("test")
                .overallRating(4.5)
                .hygieneScore(4.0)
                .locationScore(4.5)
                .amenitiesScore(4.5)
                .build();

        // Call the method
        boardService.createBoard(user, boardDto, encoded, multipartFile);

        // Verify interactions
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
        verify(boardRepository, times(1)).save(any());
    }
}
