package com.example.demo.calendar.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CalendarSearchDto{
    private List<ScheduleDto> scheduleDtos = new ArrayList<>();

    public void addScheduleDto(ScheduleDto s){
        this.scheduleDtos.add(s);
    }
}
