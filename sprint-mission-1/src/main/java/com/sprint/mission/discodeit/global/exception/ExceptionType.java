package com.sprint.mission.discodeit.global.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum ExceptionType {
    USER_NOT_FOUND(
        Level.WARN,
        HttpStatus.NOT_FOUND,
        "찾으시는 유저가 존재하지 않습니다"
    ),
    USER_NAME_CONFLICT(
        Level.WARN,
        HttpStatus.CONFLICT,
        "이미 존재하는 유저의 이름입니다"
    ),
    USER_EMAIL_CONFLICT(
        Level.WARN,
        HttpStatus.CONFLICT,
        "이미 존재하는 유저의 이메일입니다"
    ),


    USER_STATUS_NOT_FOUND(
        Level.WARN,
        HttpStatus.NOT_FOUND,
        "찾으시는 유저 상태가 존재하지 않습니다"
    ),
    USER_STATUS_MISSING_FOR_USER(
        Level.ERROR,
        HttpStatus.INTERNAL_SERVER_ERROR,
        "해당 유저 상태 정보를 처리할 수 없습니다"
    ),
    USER_STATUS_CONFLICT(
        Level.WARN,
        HttpStatus.CONFLICT,
        "이미 존재하는 유저의 상태입니다"
    ),


    CHANNEL_NOT_FOUND(
        Level.WARN,
        HttpStatus.NOT_FOUND,
        "찾으시는 채널이 존재하지 않습니다"
    ),
    CHANNEL_NAME_CONFLICT(
        Level.WARN,
        HttpStatus.CONFLICT,
        "이미 존재하는 채널의 이름입니다"
    ),
    PRIVATE_CHANNEL_UPDATE_DENIED(
        Level.WARN,
        HttpStatus.FORBIDDEN,
        "비공개 채널은 수정할 수 없습니다"
    ),


    MESSAGE_NOT_FOUND(
        Level.WARN,
        HttpStatus.NOT_FOUND,
        "찾으시는 메세지가 존재하지 않습니다"
    ),

    BINARY_CONTENT_NOT_FOUND(
        Level.WARN,
        HttpStatus.NOT_FOUND,
        "찾으시는 이미지가 존재하지 않습니다"
    ),


    AUTH_INVALID(
        Level.WARN,
        HttpStatus.UNAUTHORIZED,
        "아이디 또는 비밀번호가 올바르지 않습니다"
    ),

    READ_STATUS_CONFLICT(
        Level.WARN,
        HttpStatus.CONFLICT,
        "이미 존재하는 읽음 상태입니다"
    ),

    VALIDATION_FAILED(
        Level.WARN,
        HttpStatus.BAD_REQUEST,
        "요청 값이 올바르지 않습니다"
    ),

    INVALID_PARAMETER(
        Level.WARN,
        HttpStatus.BAD_REQUEST,
        "요청 파라미터 형식이 올바르지 않습니다."
    ),

    MULTIPART_FAILED(
        Level.WARN,
        HttpStatus.BAD_REQUEST,
        "파일 처리에서 오류가 발생했습니다."
    ),

    INTERNAL_ERROR(
        Level.ERROR,
        HttpStatus.INTERNAL_SERVER_ERROR,
        "서버 내부 오류가 발생했습니다"
    );

    Level level;
    HttpStatus status;
    String message;
}
