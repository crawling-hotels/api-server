package com.example.demo.calendar.controller;

import com.example.demo.calendar.domain.Calendar;
import com.example.demo.calendar.exception.ScheduleNotFoundException;
import com.example.demo.calendar.repository.ScheduleRepository;
import com.example.demo.calendar.service.CalendarService;
import com.example.demo.user.domain.User;
import com.example.demo.util.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.*;

import static com.example.demo.common.constant.ResponseCodeEnum.SCHEDULE_NOT_FOUND;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CalendarControllerTest {
    @InjectMocks
    private CalendarService calendarService;

    @Mock
    private ScheduleRepository scheduleRepository;


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
}
