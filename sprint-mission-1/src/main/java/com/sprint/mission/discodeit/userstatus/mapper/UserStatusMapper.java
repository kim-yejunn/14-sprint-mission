package com.sprint.mission.discodeit.userstatus.mapper;

import com.sprint.mission.discodeit.userstatus.dto.UserStatusDto;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {

    @Mapping(target = "userId", source = "user.id")
    UserStatusDto toDto(UserStatus userStatus);
}