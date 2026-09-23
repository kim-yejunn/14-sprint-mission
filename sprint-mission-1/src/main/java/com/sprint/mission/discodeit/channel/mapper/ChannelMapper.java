package com.sprint.mission.discodeit.channel.mapper;

import com.sprint.mission.discodeit.channel.dto.ChannelDto;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ChannelMapper {

    @Mapping(target = "participants", source = "participants")
    @Mapping(target = "lastMessageAt", source = "lastMessageAt")
    ChannelDto toDto(Channel channel, List<User> participants, Instant lastMessageAt);
}
