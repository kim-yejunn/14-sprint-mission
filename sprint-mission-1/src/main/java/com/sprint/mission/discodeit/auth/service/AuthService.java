package com.sprint.mission.discodeit.auth.service;

import com.sprint.mission.discodeit.auth.dto.LoginRequestDto;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import com.sprint.mission.discodeit.user.dto.UserResponse;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Transactional
    public UserResponse login(LoginRequestDto loginRequestDto) {
        User user = (User) userRepository.findByUserName(loginRequestDto.username())
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.AUTH_INVALID
            ));

        if (!user.getPassword().equals(loginRequestDto.password())) {
            throw new DiscodeitException(
                ExceptionType.AUTH_INVALID
            );
        }

        UserStatus userStatus = userStatusRepository.findByUser(user)
            .orElseThrow(
                () -> new DiscodeitException(
                    ExceptionType.USER_STATUS_MISSING_FOR_USER,
                    Map.of("userId", user.getId()
                    )));

        userStatus.userLogin();
        userStatusRepository.save(userStatus);

        return UserResponse.from(user);
    }
}
