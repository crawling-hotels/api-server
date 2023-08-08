package com.example.demo.image.exception;

public class ImagePathExtractFailException extends RuntimeException{
    private static final long serialVersionUID = 1;

    public ImagePathExtractFailException(String message)
    {
        super(message);
    }
}
