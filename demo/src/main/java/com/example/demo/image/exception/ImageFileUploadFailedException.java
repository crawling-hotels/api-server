package com.example.demo.image.exception;

public class ImageFileUploadFailedException extends RuntimeException{
    private static final long serialVersionUID = 1;

    public ImageFileUploadFailedException(String message)
    {
        super(message);
    }
}
