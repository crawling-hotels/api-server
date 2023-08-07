package com.example.demo.board.domain;

import com.example.demo.hotel.domain.Hotel;
import com.example.demo.user.domain.User;
import com.example.demo.util.config.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private String imagePath;

    private double overallRating; // 전체 평점

    private double hygieneScore;  // 위생 점수

    private double locationScore; // 위치 접근성 점수

    private double amenitiesScore; // 비품 만족도 점수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @Builder
    public Board(String title, String content, String imagePath,
                 double overallRating, double hygieneScore, double locationScore, double amenitiesScore) {
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.overallRating = overallRating;
        this.hygieneScore = hygieneScore;
        this.locationScore = locationScore;
        this.amenitiesScore = amenitiesScore;
    }

    public void update(String title, String content, String imagePath) {
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
    }
}
