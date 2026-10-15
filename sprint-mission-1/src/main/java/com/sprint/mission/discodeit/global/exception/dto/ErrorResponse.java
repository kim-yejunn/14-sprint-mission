package com.sprint.mission.discodeit.global.exception.dto;

import com.sprint.mission.discodeit.global.exception.ExceptionType;

public record ErrorResponse(
    String code,
    String message
) {

    public static ErrorResponse of(ExceptionType type) {
        return new ErrorResponse(type.name(), type.getMessage());
    }

    public static ErrorResponse of(ExceptionType type, String message) {
        return new ErrorResponse(type.name(), message);
    }
}