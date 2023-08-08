package com.example.demo.image.exception;

public class ImageFileEmptyException extends RuntimeException{
    private static final long serialVersionUID = 1;

    public ImageFileEmptyException(String message)
    {
        super(message);
    }
}