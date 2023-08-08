package com.example.demo.image.exception;

public class ImageFileDeleteFailedException extends RuntimeException{
    private static final long serialVersionUID = 1;

    public ImageFileDeleteFailedException(String message)
    {
        super(message);
    }
}