package com.sprint.mission.discodeit.userstatus.entity;

import com.sprint.mission.discodeit.global.entity.BaseUpdatableEntity;
import com.sprint.mission.discodeit.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends BaseUpdatableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    private Instant lastActiveAt;

    private UserStatus(User user) {
        this.user = user;
        this.lastActiveAt = Instant.now();
        user.assignStatus(this);
    }

    private void online() {
        this.lastActiveAt = Instant.now();
    }

    public static UserStatus create(User user) {
        return new UserStatus(user);
    }

    public void userLogin() {
        online();
        super.markUpdated();
    }

    public boolean isOnline() {
        return lastActiveAt != null
            && Duration.between(lastActiveAt, Instant.now()).toMinutes() < 5;
    }
}
