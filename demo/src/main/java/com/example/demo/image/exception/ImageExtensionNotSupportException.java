package com.example.demo.image.exception;

public class ImageExtensionNotSupportException extends RuntimeException{
    private static final long serialVersionUID = 1;

    public ImageExtensionNotSupportException(String message)
    {
        super(message);
    }
}