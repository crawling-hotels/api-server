package com.example.demo.board.exception;

public class URLDecodeFailException extends RuntimeException{
    private static final long serialVersionUID = 1;

    public URLDecodeFailException(String message)
    {
        super(message);
    }
}