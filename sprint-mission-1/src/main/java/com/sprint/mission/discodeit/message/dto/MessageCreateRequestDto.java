package com.sprint.mission.discodeit.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequestDto(
    @NotNull
    UUID authorId,
    @NotNull
    UUID channelId,
    @NotBlank
    String content) {

}
