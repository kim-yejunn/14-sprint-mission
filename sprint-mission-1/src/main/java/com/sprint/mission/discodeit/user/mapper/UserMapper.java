package com.sprint.mission.discodeit.user.mapper;

import com.sprint.mission.discodeit.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.user.dto.UserDto;
import com.sprint.mission.discodeit.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public interface UserMapper {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "online", expression = "java(user.getStatus() != null && user.getStatus().isOnline())")
    UserDto toDto(User user);
}