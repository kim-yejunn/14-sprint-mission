package com.sprint.mission.discodeit.readstatus.dto;

import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponseDto(UUID id, Instant createdAt, Instant updatedAt, UUID userId,
                                    UUID channelId, Instant lastReadAt) {

    public static ReadStatusResponseDto from(ReadStatus readStatus) {
        return new ReadStatusResponseDto(
            readStatus.getId(),
            readStatus.getCreatedAt(),
            readStatus.getUpdatedAt(),
            readStatus.getUser().getId(),
            readStatus.getChannel().getId(),
            readStatus.getLastReadAt()
        );
    }
}
