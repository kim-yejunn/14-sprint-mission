package com.sprint.mission.discodeit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentDto;
import java.util.UUID;

public record UserDto(UUID id,
                      @JsonProperty("username")
                      String userName,
                      String email,
                      BinaryContentDto profile,
                      boolean online) {

}
