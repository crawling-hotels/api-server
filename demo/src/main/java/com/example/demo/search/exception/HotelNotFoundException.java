package com.example.demo.search.exception;

public class HotelNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1;

    public HotelNotFoundException(String message) {
        super(message);
    }
}
