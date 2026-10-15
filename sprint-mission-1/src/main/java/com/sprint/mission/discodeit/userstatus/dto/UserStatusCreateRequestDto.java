package com.sprint.mission.discodeit.userstatus.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UserStatusCreateRequestDto(
    @NotNull
    UUID userId) {

}
