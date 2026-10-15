package com.sprint.mission.discodeit.global.exception;

import java.util.Map;
import lombok.Getter;

@Getter
public class DiscodeitException extends RuntimeException {

    private final ExceptionType type;
    private final Map<String, Object> details;

    public DiscodeitException(ExceptionType type) {
        super(type.getMessage());
        this.type = type;
        this.details = Map.of();
    }

    public DiscodeitException(ExceptionType type, Map<String, Object> details) {
        super(type.getMessage());
        this.type = type;
        this.details = details;
    }

    public DiscodeitException(ExceptionType type, Map<String, Object> details, Throwable cause) {
        super(type.getMessage(), cause);
        this.type = type;
        this.details = details;
    }
}
