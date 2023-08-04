package com.example.demo.board.dto;

import lombok.Getter;

@Getter
public class BoardDto {
    private Long boardId;
    private String title;
    private String content;
    private String imagePath;

    public BoardDto(Long boardId, String title, String content, String imagePath) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
    }
}
