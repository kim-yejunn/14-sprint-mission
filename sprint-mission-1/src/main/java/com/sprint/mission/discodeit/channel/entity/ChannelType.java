package com.sprint.mission.discodeit.channel.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum ChannelType {
    PUBLIC("PUBLIC", "공개 채널"),
    PRIVATE("PRIVATE", "비공개 채널");

    @JsonValue
    String channelType;
    String description;

    @JsonCreator
    public static ChannelType of(String channelType) {
        return Arrays.stream(ChannelType.values())
            .filter(type -> type.getChannelType().equalsIgnoreCase(channelType))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널 타입: " + channelType));
    }
}
