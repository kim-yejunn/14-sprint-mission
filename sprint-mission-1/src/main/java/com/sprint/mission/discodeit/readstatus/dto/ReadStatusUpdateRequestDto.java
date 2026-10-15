package com.sprint.mission.discodeit.readstatus.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record ReadStatusUpdateRequestDto(
    @NotNull
    Instant newLastReadAt) {

}
