package com.sprint.mission.discodeit.channel.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public record ChannelPrivateCreateRequestDto(
    @NotEmpty
    List<UUID> participantIds) {

}
