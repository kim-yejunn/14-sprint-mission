package com.sprint.mission.discodeit.message.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateRequestDto(
    @NotBlank
    String newContent) {

}
