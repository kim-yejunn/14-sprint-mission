package com.sprint.mission.discodeit.message.dto;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.message.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponseDto(UUID id, Instant createdAt, Instant updatedAt, String content,
                                 UUID channelId, UUID authorId, List<UUID> attachmentIds) {

    public static MessageResponseDto from(Message message) {
        return new MessageResponseDto(
            message.getId(),
            message.getCreatedAt(),
            message.getUpdatedAt(),
            message.getContent(),
            message.getChannel().getId(),
            message.getAuthor().getId(),
            message.getAttachments()
                .stream()
                .map(BinaryContent::getId)
                .toList()
        );
    }
}
