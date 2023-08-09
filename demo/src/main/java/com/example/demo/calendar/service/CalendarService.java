package com.example.demo.calendar.service;

import com.example.demo.calendar.domain.Calendar;
import com.example.demo.calendar.domain.Schedule;
import com.example.demo.calendar.dto.CalendarSearchDto;
import com.example.demo.calendar.dto.ScheduleDto;
import com.example.demo.calendar.exception.ScheduleNotFoundException;
import com.example.demo.calendar.repository.CalendarRepository;
import com.example.demo.calendar.repository.ScheduleRepository;
import com.example.demo.common.constant.ResponseCodeEnum;
import com.example.demo.common.dto.MessageResponse;
import com.example.demo.hotel.domain.Hotel;
import com.example.demo.hotel.repository.HotelRepository;
import com.example.demo.hotel.service.HotelService;
import com.example.demo.search.exception.HotelNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.exception.UserNotFoundException;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ScheduleRepository scheduleRepository;

    @Autowired
    private final CalendarRepository calendarRepository;

    @Autowired
    private final HotelRepository hotelRepository;

    @Transactional
    public MessageResponse getCalendar(User user){
        if (user.getCalendar() == null) {
            user.setCalendar(new Calendar());

            userRepository.save(user);
        }
        Calendar calendar = user.getCalendar();

        CalendarSearchDto searchDto = new CalendarSearchDto();
        for(Schedule s: calendar.getSchedules()){
            searchDto.addScheduleDto(ScheduleDto.createScheduleDto(s));
        }

        return MessageResponse.of(ResponseCodeEnum.CALENDAR_SEARCH_SUCCESS, searchDto);
    }

    @Transactional
    public void addSchedule(User user, ScheduleDto scheduleDto){
        if (user.getCalendar() == null) {
            user.setCalendar(new Calendar());

            userRepository.save(user);
        }
        Calendar calendar = user.getCalendar();

        Hotel hotel = hotelRepository.findByName(scheduleDto.getName())
                .orElseThrow(() -> new HotelNotFoundException(ResponseCodeEnum.HOTEL_NOT_FOUND.getMessage()));

        Schedule schedule = new Schedule(hotel, scheduleDto.getCheckinDate(), scheduleDto.getCheckoutDate());
        calendar.addSchedule(schedule);
        hotel.addSchedule(schedule);

        scheduleRepository.save(schedule);
    }

    @Transactional
    public void removeSchedule(User user, Long scheduleId){
        Calendar calendar = user.getCalendar();

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException(ResponseCodeEnum.SCHEDULE_NOT_FOUND.getMessage()));

        calendar.getSchedules().remove(schedule);
        schedule.getHotel().getSchedules().remove(schedule);

        scheduleRepository.delete(schedule);
    }

    @Transactional
    public void shareCalendar(User user, Long inviteeId){
        Calendar calendar = user.getCalendar();

        User invitee = userRepository.findById(inviteeId)
                .orElseThrow(() -> new UserNotFoundException(ResponseCodeEnum.USER_NOT_FOUND.getMessage()));

        calendar.addUser(invitee);
        calendarRepository.save(calendar);
    }
}
