package com.example.demo.calendar.dto;

import com.example.demo.calendar.domain.Schedule;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
public class ScheduleDto implements Serializable {
    @JsonProperty
    private String name;
    @JsonProperty
    private LocalDate checkinDate;
    @JsonProperty
    private LocalDate checkoutDate;

    public ScheduleDto(String name, LocalDate checkinDate, LocalDate checkoutDate) {
        this.name = name;
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
    }

    public static ScheduleDto createScheduleDto(Schedule schedule){
        return new ScheduleDto(
            schedule.getHotel().getName(),
            schedule.getCheckinDate(),
            schedule.getCheckoutDate()
        );
    }
}
