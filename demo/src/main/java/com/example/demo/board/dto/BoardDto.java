package com.example.demo.board.dto;

import lombok.Getter;

@Getter
public class BoardDto {
    private Long boardId;
    private String title;
    private String content;
    private String imagePath;
    private double overallRating; // 전체 평점

    private double hygieneScore;  // 위생 점수

    private double locationScore; // 위치 접근성 점수

    private double amenitiesScore; // 비품 만족도 점수

    public BoardDto(Long boardId, String title, String content, String imagePath,
                    double overallRating, double hygieneScore, double locationScore, double amenitiesScore) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.overallRating = overallRating;
        this.hygieneScore = hygieneScore;
        this.locationScore = locationScore;
        this.amenitiesScore = amenitiesScore;
    }
}
