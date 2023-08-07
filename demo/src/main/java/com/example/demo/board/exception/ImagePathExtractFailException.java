package com.example.demo.board.exception;

public class ImagePathExtractFailException extends RuntimeException{
    private static final long serialVersionUID = 1;

    public ImagePathExtractFailException(String message)
    {
        super(message);
    }
}
