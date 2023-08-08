package com.example.demo.board.exception;

public class BoardNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1;

    public BoardNotFoundException(String message)
    {
        super(message);
    }
}
