package com.example.demo.user.domain;

import com.example.demo.board.domain.Board;
import com.example.demo.board.domain.Comment;
import com.example.demo.calendar.domain.Calendar;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "user")
    private List<Board> boards;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "user")
    private List<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

}

