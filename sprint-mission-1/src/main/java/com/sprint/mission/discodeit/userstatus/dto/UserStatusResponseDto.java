package com.sprint.mission.discodeit.userstatus.dto;

import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;

public record UserStatusResponseDto(UUID id, Instant createdAt, Instant updatedAt, UUID userId,
                                    boolean online, Instant lastActiveAt) {

    public static UserStatusResponseDto from(UserStatus userStatus) {
        return new UserStatusResponseDto(
            userStatus.getId(),
            userStatus.getCreatedAt(),
            userStatus.getUpdatedAt(),
            userStatus.getUser().getId(),
            userStatus.isOnline(),
            userStatus.getLastActiveAt()
        );
    }
}
