package com.example.demo.calendar.exception;

public class ScheduleNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1;

    public ScheduleNotFoundException(String message)
    {
        super(message);
    }
}
