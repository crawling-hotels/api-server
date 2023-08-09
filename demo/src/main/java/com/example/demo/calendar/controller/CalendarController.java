package com.example.demo.calendar.controller;

import com.example.demo.calendar.dto.ScheduleDto;
import com.example.demo.calendar.service.CalendarService;
import com.example.demo.common.dto.MessageResponse;
import com.example.demo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {
    @Autowired
    private CalendarService calendarService;

    @GetMapping
    public ResponseEntity<MessageResponse> view(@AuthenticationPrincipal User user){
        return ResponseEntity.ok().body(calendarService.getCalendar(user));
    }

    @PutMapping("/save")
    public ResponseEntity<Void> update(@AuthenticationPrincipal User user,
                                    @RequestBody ScheduleDto scheduleDto){
        calendarService.addSchedule(user, scheduleDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> remove(@AuthenticationPrincipal User user,
                                    @PathVariable Long scheduleId){
        calendarService.removeSchedule(user, scheduleId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/invite")
    public ResponseEntity<Void> invite(@AuthenticationPrincipal User user,
                                                  @RequestBody Long inviteeId){
        calendarService.shareCalendar(user, inviteeId);
        return ResponseEntity.ok().build();
    }

//    @DeleteMapping
//    public ResponseEntity<?> init(@AuthenticationPrincipal User user){
//        calendarService.clearSchedule(user);
//        return ResponseEntity.noContent();
//    }


}
