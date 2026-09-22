package com.sprint.mission.discodeit.userstatus.entity;

import com.sprint.mission.discodeit.global.entity.BaseUpdatableEntity;
import com.sprint.mission.discodeit.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private Instant lastActiveAt;

    public UserStatus(User user) {
        this.user = user;
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
