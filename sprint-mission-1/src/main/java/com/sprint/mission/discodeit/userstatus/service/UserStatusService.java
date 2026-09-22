package com.sprint.mission.discodeit.userstatus.service;

import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
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

    @Transactional
    public UserStatusResponseDto userStatusCreate(
        UserStatusCreateRequestDto userStatusCreateRequestDto) {

        User user = userRepository.findById(userStatusCreateRequestDto.userId())
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_NOT_FOUND,
                Map.of("userId", userStatusCreateRequestDto.userId())
            ));

        if (userStatusRepository.findById(userStatusCreateRequestDto.userId()).isPresent()) {
            throw new DiscodeitException(
                ExceptionType.USER_STATUS_CONFLICT,
                Map.of("userId", userStatusCreateRequestDto.userId())
            );
        }

        return UserStatusResponseDto.from(
            userStatusRepository.save(new UserStatus(user)));
    }

    @Transactional
    public UserStatusResponseDto userStatusUpdate(UUID userStatusId,
        UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        UserStatus userStatus = userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_STATUS_NOT_FOUND,
                Map.of("userStatusId", userStatusId)
            ));

        if (userStatusUpdateRequestDto.newLastActiveAt() != null) {
            userStatus.updateAt(userStatusUpdateRequestDto.newLastActiveAt());
        }

        userStatusRepository.save(userStatus);

        return UserStatusResponseDto.from(userStatus);
    }

    @Transactional
    public UserStatusResponseDto userStatusUpdateByUserId(UUID userId,
        UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        UserStatus userStatus = userStatusRepository.findById(userId).orElseThrow((
            () -> new DiscodeitException(
                ExceptionType.USER_STATUS_MISSING_FOR_USER,
                Map.of("userId", userId
                ))));

        if (userStatusUpdateRequestDto.newLastActiveAt() != null) {
            userStatus.updateAt(userStatusUpdateRequestDto.newLastActiveAt());
        }

        userStatusRepository.save(userStatus);

        return UserStatusResponseDto.from(userStatus);
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


    public List<UserStatusResponseDto> findAllByUserId(UUID userId) {
        List<UserStatus> userStatuses = userStatusRepository.findAll();

        return userStatuses.stream()
            .filter(userStatus -> userStatus.getUser().getId().equals(userId))
            .map(UserStatusResponseDto::from)
            .toList();
    }


    public UserStatusResponseDto findUserStatus(UUID userStatusId) {
        return UserStatusResponseDto.from(userStatusRepository.findById(userStatusId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_STATUS_NOT_FOUND,
                Map.of("userStatusId", userStatusId)
            )));
    }
}
