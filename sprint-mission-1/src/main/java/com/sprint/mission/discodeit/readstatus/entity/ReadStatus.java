package com.sprint.mission.discodeit.readstatus.entity;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.global.entity.BaseUpdatableEntity;
import com.sprint.mission.discodeit.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "channel_id"}))
public class ReadStatus extends BaseUpdatableEntity {
    // 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델
    // 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private Instant lastReadAt;

    public ReadStatus(Channel channel, User user) {
        this(channel, user, Instant.now());
    }

    public ReadStatus(Channel channel, User user, Instant lastReadAt) {
        this.channel = channel;
        this.user = user;
        this.lastReadAt = lastReadAt;
        super.markUpdated();
    }

    public void updateAt(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}
