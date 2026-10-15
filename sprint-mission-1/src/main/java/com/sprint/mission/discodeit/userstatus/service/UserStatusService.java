package com.sprint.mission.discodeit.userstatus.service;

import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusDto;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.userstatus.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStatusService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserStatusMapper userStatusMapper;

    @Transactional
    public UserStatusDto userStatusCreate(
        UserStatusCreateRequestDto userStatusCreateRequestDto) {

        User user = userRepository.findById(userStatusCreateRequestDto.userId())
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_NOT_FOUND,
                Map.of("userId", userStatusCreateRequestDto.userId())
            ));

        if (userStatusRepository.findByUserId(userStatusCreateRequestDto.userId()).isPresent()) {
            throw new DiscodeitException(
                ExceptionType.USER_STATUS_CONFLICT,
                Map.of("userId", userStatusCreateRequestDto.userId())
            );
        }

        return userStatusMapper.toDto(
            userStatusRepository.save(UserStatus.create(user)));
    }

    @Transactional
    public UserStatusDto userStatusUpdate(UUID userStatusId,
        UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_STATUS_NOT_FOUND,
                Map.of("userStatusId", userStatusId)
            ));

        if (userStatusUpdateRequestDto.newLastActiveAt() != null) {
            userStatus.updateAt(userStatusUpdateRequestDto.newLastActiveAt());
        }

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    public UserStatusDto userStatusUpdateByUserId(UUID userId,
        UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        UserStatus userStatus = userStatusRepository.findByUserId((userId)).orElseThrow((
            () -> new DiscodeitException(
                ExceptionType.USER_STATUS_MISSING_FOR_USER,
                Map.of("userId", userId
                ))));

        if (userStatusUpdateRequestDto.newLastActiveAt() != null) {
            userStatus.updateAt(userStatusUpdateRequestDto.newLastActiveAt());
        }

        return userStatusMapper.toDto(userStatus);
    }

    @Transactional
    public void userStatusDelete(UUID userStatusId) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_STATUS_NOT_FOUND,
                Map.of("userStatusId", userStatusId)
            ));

        userStatusRepository.delete(userStatus);
    }


    public List<UserStatusDto> findAllByUserId(UUID userId) {
        List<UserStatus> userStatuses = userStatusRepository.findAll();

        return userStatuses.stream()
            .filter(userStatus -> userStatus.getUser().getId().equals(userId))
            .map(userStatusMapper::toDto)
            .toList();
    }


    public UserStatusDto findUserStatus(UUID userStatusId) {
        return userStatusMapper.toDto(userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_STATUS_NOT_FOUND,
                Map.of("userStatusId", userStatusId)
            )));
    }
}
