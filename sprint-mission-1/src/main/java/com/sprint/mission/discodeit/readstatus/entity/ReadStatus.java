package com.sprint.mission.discodeit.readstatus.entity;

import com.sprint.mission.discodeit.global.entity.BaseEntity;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ReadStatus extends BaseEntity {
    // 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델
    // 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용

    private final UUID channelId;
    private final UUID userId;
    private Instant lastReadAt;

    public ReadStatus(UUID channelId, UUID userId) {
        this(channelId, userId, Instant.now());
    }

    public ReadStatus(UUID channelId, UUID userId, Instant lastReadAt) {
        this.channelId = channelId;
        this.userId = userId;
        this.lastReadAt = lastReadAt;
        super.markUpdated();
    }

    public void updateAt(Instant lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}
