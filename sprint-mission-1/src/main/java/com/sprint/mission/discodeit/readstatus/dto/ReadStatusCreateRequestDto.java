package com.sprint.mission.discodeit.readstatus.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequestDto(
    @NotNull
    UUID channelId,
    @NotNull
    UUID userId,
    Instant lastReadAt) {

}
