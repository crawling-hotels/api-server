package com.example.demo.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCodeEnum {
    SCHEDULE_NOT_FOUND("해당 id를 가진 스케줄을 찾을 수 없습니다.", -1),
    USER_NOT_FOUND("해당 id를 가진 유저를 찾을 수 없습니다.", -1),
    HOTEL_NOT_FOUND("해당 id를 가진 호텔을 찾을 수 없습니다.", -1),
    URL_DECODE_FAILED("인코딩된 문자열을 디코드하지 못했습니다.", -1),
    FAVORITE_NOT_FOUND("해당 id를 가진 찜하기를 찾을 수 없습니다.", -1),
    BOARD_NOT_FOUND("해당 id를 가진 리뷰를 찾을 수 없습니다.", -1),
    BOARD_WRITER_NOT_SAME("해당 리뷰를 작성한 게시자가 아닙니다.", -1),
    CALENDAR_SEARCH_SUCCESS("캘린더 조회를 성공했습니다.", 1),
    HOTEL_SEARCH_SUCCESS("구간별 가격비교 데이터 조회를 성공했습니다.", 1),
    DETAIL_SEARCH_SUCCESS("호텔 디테일 데이터 조회를 성공했습니다.", 1),
    FAVORITE_SEARCH_SUCCESS("찜하기 데이터 조회를 성공했습니다.", 1),
    BOARD_SEARCH_SUCCESS("게시글 조회를 성공했습니다.", 1),
    SIMPLE_REQUEST_FAILURE("요청을 처리하던 중 오류가 발생했습니다." , -1);

    private final String message;
    private final int code;
}
