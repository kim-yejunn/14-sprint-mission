package com.sprint.mission.discodeit.channel.dto;

import jakarta.validation.constraints.NotBlank;

public record ChannelPublicCreateRequestDto(
    @NotBlank
    String name,
    @NotBlank
    String description) {

}
