package com.sprint.mission.discodeit.user.dto;

import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;

public record UserDto(UUID id, Instant createdAt, Instant updatedAt, String username,
                      String email, UUID profileId,
                      boolean online) {

    public static UserDto from(User user, UserStatus userStatus) {
        return new UserDto(
            user.getId(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getUserName(),
            user.getEmail(),
            user.getProfile().getId(),
            userStatus.isOnline()
        );
    }
}
