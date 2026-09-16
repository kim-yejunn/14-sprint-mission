package com.sprint.mission.discodeit.userstatus.entity;

import com.sprint.mission.discodeit.global.entity.BaseEntity;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserStatus extends BaseEntity {

    @NotNull
    private UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId) {
        this.userId = userId;
    }

    public void userLogin() {
        super.markUpdated();
    }

    public boolean isOnline() {
        return lastActiveAt != null
            && Duration.between(lastActiveAt, Instant.now()).toMinutes() < 5;
    }

    public void updateAt(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
    }
}
