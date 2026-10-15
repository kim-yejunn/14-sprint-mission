package com.sprint.mission.discodeit.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.dto.UserCreateRequestDto;
import com.sprint.mission.discodeit.user.dto.UserDto;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.user.service.UserService;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("사용자 생성 성공")
    void userCreate_success() {
        UserCreateRequestDto request = new UserCreateRequestDto("kyj", "pw1234", "a@b.com");
        given(userRepository.findByUserName("kyj")).willReturn(Optional.empty());
        given(userRepository.findByEmail("a@b.com")).willReturn(Optional.empty());

        User user = User.create("kyj", "pw1234", "a@b.com", null);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(userMapper.toDto(user)).willReturn(
            new UserDto(user.getId(), "kyj", "a@b.com", null, true));

        UserDto result = userService.userCreate(request, null);

        assertThat(result.userName()).isEqualTo("kyj");
        then(userStatusRepository).should().save(any(UserStatus.class));
    }

    @Test
    @DisplayName("사용자 생성 실패 - 이름 중복")
    void userCreate_fail_duplicateUserName() {
        UserCreateRequestDto request = new UserCreateRequestDto("kyj", "pw1234", "a@b.com");
        given(userRepository.findByUserName("kyj")).willReturn(Optional.of(mock(User.class)));

        DiscodeitException exception = assertThrows(DiscodeitException.class,
            () -> userService.userCreate(request, null));

        assertThat(exception.getType()).isEqualTo(ExceptionType.USER_NAME_CONFLICT);
        then(userRepository).should(never()).save(any());
    }
}
