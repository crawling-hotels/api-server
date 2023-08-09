package com.example.demo.calendar.controller;

import com.example.demo.calendar.domain.Calendar;
import com.example.demo.calendar.domain.Schedule;
import com.example.demo.calendar.dto.ScheduleDto;
import com.example.demo.calendar.exception.ScheduleNotFoundException;
import com.example.demo.calendar.repository.CalendarRepository;
import com.example.demo.calendar.repository.ScheduleRepository;
import com.example.demo.calendar.service.CalendarService;
import com.example.demo.hotel.domain.Hotel;
import com.example.demo.hotel.repository.HotelRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.util.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CalendarControllerTest {
    @InjectMocks
    private CalendarService calendarService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private CalendarRepository calendarRepository;


    @Test
    @DisplayName("스케줄 삭제 실패 - 존재하지 않은 스케줄")
    void 스케줄_삭제_실패() throws Exception {
        // Given
        User mockUser = mock(User.class);
        Calendar mockCalendar = mock(Calendar.class);

        doReturn(Optional.empty()).when(scheduleRepository).findById(anyLong());
        doReturn(mockCalendar).when(mockUser).getCalendar();

        // When
        Throwable thrown = catchThrowable(() -> calendarService.removeSchedule(mockUser, 1L));

        // Then
        assertThat(thrown).isInstanceOf(ScheduleNotFoundException.class).hasMessage("해당 id를 가진 스케줄을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("유저에 캘린더가 없다면 캘린더를 생성한다.")
    void 캘린더_없을시_생성_성공(){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = User.builder()
                .username("user2")
                .password(passwordEncoder.encode("user2Pass"))
                .role("USER")
                .build();

        calendarService.getCalendar(user);

        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("캘린더에 새로운 스케줄을 성공적으로 추가합니다.")
    void 캘린더_새로운_스케줄_추가_성공(){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = User.builder()
                .username("user2")
                .password(passwordEncoder.encode("user2Pass"))
                .role("USER")
                .build();

        Calendar calendar = new Calendar();
        user.setCalendar(calendar);

        ScheduleDto scheduleDto = new ScheduleDto(
                "강릉 호텔",
                LocalDate.of(2023, 8, 10),
                LocalDate.of(2023, 8, 12)
        );

        Hotel hotel = new Hotel("강릉 호텔");
        when(hotelRepository.findByName(any(String.class))).thenReturn(Optional.ofNullable(hotel));

        calendarService.addSchedule(user, scheduleDto);

        assertEquals(1, calendar.getSchedules().size());
        assertEquals(1, hotel.getSchedules().size());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    @DisplayName("캘린더에 새로운 스케줄을 성공적으로 삭제합니다.")
    void 캘린더_새로운_스케줄_삭제_성공(){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = User.builder()
                .username("user2")
                .password(passwordEncoder.encode("user2Pass"))
                .role("USER")
                .build();

        Calendar calendar = new Calendar();
        user.setCalendar(calendar);

        Hotel hotel = new Hotel("강릉 호텔");

        Schedule schedule = new Schedule(
                hotel,
                LocalDate.of(2023, 8, 10),
                LocalDate.of(2023, 8, 12)
        );
        calendar.addSchedule(schedule);
        hotel.addSchedule(schedule);

        when(scheduleRepository.findById(null)).thenReturn(Optional.of(schedule));

        calendarService.removeSchedule(user, schedule.getId());
        assertEquals(0, calendar.getSchedules().size());
        assertEquals(0, hotel.getSchedules().size());
        verify(scheduleRepository, times(1)).delete(any(Schedule.class));
    }

    @Test
    @DisplayName("다른 유저와 캘린더를 공유합니다.")
    void 캘린더_유저_초대_성공(){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user2 = User.builder()
                .username("user2")
                .password(passwordEncoder.encode("user2Pass"))
                .role("USER")
                .build();

        User invitee = User.builder()
                .username("user3")
                .password(passwordEncoder.encode("user3Pass"))
                .role("USER")
                .build();

        Calendar calendar2 = new Calendar();
        Calendar calendarInvitee = new Calendar();
        user2.setCalendar(calendar2);
        invitee.setCalendar(calendarInvitee);

        when(userRepository.findById(null)).thenReturn(Optional.of(invitee));

        calendarService.shareCalendar(user2, invitee.getId());

        assertEquals(user2.getCalendar(), invitee.getCalendar());
    }
}
