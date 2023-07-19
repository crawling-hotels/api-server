package com.example.demo.board.domain;

import com.example.demo.hotel.domain.Hotel;
import com.example.demo.util.config.BaseEntity;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.h2.engine.User;

import java.util.List;

@Entity
@NoArgsConstructor
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comment")
    private List<Comment> comments;

    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    private Hotel hotel;
}