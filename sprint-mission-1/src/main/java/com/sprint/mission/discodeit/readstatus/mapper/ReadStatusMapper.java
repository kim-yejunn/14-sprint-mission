package com.sprint.mission.discodeit.readstatus.mapper;

import com.sprint.mission.discodeit.readstatus.dto.ReadStatusDto;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReadStatusMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "channelId", source = "channel.id")
    ReadStatusDto toDto(ReadStatus readStatus);
}
