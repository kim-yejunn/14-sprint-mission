package com.sprint.mission.discodeit.userstatus.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record UserStatusUpdateRequestDto(
    @NotNull
    Instant newLastActiveAt) {

}
