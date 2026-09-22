package com.sprint.mission.discodeit.user.dto;

import com.sprint.mission.discodeit.user.entity.User;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(UUID id, Instant createdAt, Instant updatedAt, String username,
                           String email, UUID profileId) {

    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getUserName(),
            user.getEmail(),
            user.getProfile().getId()
        );
    }
}
