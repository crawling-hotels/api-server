package com.example.demo.board.exception;

public class WriterNotSameException extends RuntimeException{
    private static final long serialVersionUID = 1;

    public WriterNotSameException(String message)
    {
        super(message);
    }
}
