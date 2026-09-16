package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import com.sprint.mission.discodeit.user.dto.UserCreateRequestDto;
import com.sprint.mission.discodeit.user.dto.UserDto;
import com.sprint.mission.discodeit.user.dto.UserResponse;
import com.sprint.mission.discodeit.user.dto.UserUpdateRequestDto;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse userCreate(UserCreateRequestDto userCreateRequestDto,
        MultipartFile profile) {
        if (userRepository.findByUserName(userCreateRequestDto.username()).isPresent()) {
            throw new DiscodeitException(
                ExceptionType.USER_NAME_CONFLICT,
                Map.of("userName", userCreateRequestDto.username())
            );
        }

        if (userRepository.findByUserEmail(userCreateRequestDto.email()).isPresent()) {
            throw new DiscodeitException(
                ExceptionType.USER_EMAIL_CONFLICT,
                Map.of("userEmail", userCreateRequestDto.email())
            );
        }

        UUID binaryContentsId = null;
        if (profile != null) {
            binaryContentsId = binaryContentRepository.toBinaryContent(
                profile).getId();
        }

        User user = User.create(userCreateRequestDto.username(), userCreateRequestDto.password(),
            userCreateRequestDto.email(), binaryContentsId);

        userStatusRepository.statusAdd(new UserStatus(user.getId()));

        return UserResponse.from(userRepository.userAdd(user));
    }

    @Override
    public UserResponse userUpdate(UUID userId, UserUpdateRequestDto userUpdateRequestDto,
        MultipartFile profile) {
        User user = userRepository.findByUser(userId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_NOT_FOUND,
                Map.of("userId", userId)
            ));

        if (profile != null) {
            BinaryContent binaryContent;
            try {
                binaryContent = new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes());
                binaryContentRepository.binaryAdd(binaryContent);
            } catch (IOException e) {
                throw new UncheckedIOException(
                    "파일을 읽는데 실패했습니다: " + profile.getOriginalFilename(),
                    e);
            }
            if (Objects.nonNull(user.getProfileId())) {
                binaryContentRepository.delete(user.getProfileId());
            }
            user.updateProfile(binaryContent.getId());
        }

        user.update(userUpdateRequestDto.newUsername(), userUpdateRequestDto.newPassword(),
            userUpdateRequestDto.newEmail());

        userRepository.update(user);

        userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_STATUS_MISSING_FOR_USER,
                Map.of("userId", user.getId()
                )));
        return UserResponse.from(user);
    }

    @Override
    public void userDelete(UUID userId) {
        User user = userRepository.findByUser(userId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_NOT_FOUND,
                Map.of("userId", userId)
            ));

        userStatusRepository.delete(userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_STATUS_MISSING_FOR_USER,
                Map.of("userId", user.getId()
                ))));
        if (Objects.nonNull(user.getProfileId())) {
            binaryContentRepository.delete(user.getProfileId());
        }
        userRepository.delete(user);
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAllUser();

        return users.stream()
            .map(user -> {
                UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new DiscodeitException(
                        ExceptionType.USER_STATUS_MISSING_FOR_USER,
                        Map.of("userId", user.getId()
                        )));
                return UserDto.from(user, userStatus);
            })
            .toList();
    }

    @Override
    public UserDto findById(UUID userId) {
        User user = userRepository.findByUser(userId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_NOT_FOUND,
                Map.of("userId", userId)
            ));

        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_STATUS_MISSING_FOR_USER,
                Map.of("userId", user.getId()
                )));

        return UserDto.from(user, userStatus);
    }
}
