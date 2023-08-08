package com.example.demo.board.exception;

public class URLEncodeFailException extends RuntimeException{
    private static final long serialVersionUID = 1;

    public URLEncodeFailException(String message)
    {
        super(message);
    }
}