package com.sprint.mission.discodeit.binarycontent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BinaryContentCreateRequestDto(
    @NotBlank
    String fileName,
    @NotBlank
    String contentType,
    @NotNull
    byte[] bytes) {

}
