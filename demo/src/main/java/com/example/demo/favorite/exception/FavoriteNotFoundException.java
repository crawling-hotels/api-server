package com.example.demo.favorite.exception;

public class FavoriteNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1;

    public FavoriteNotFoundException(String message)
    {
        super(message);
    }
}
