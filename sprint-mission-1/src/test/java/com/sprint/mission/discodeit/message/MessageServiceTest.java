package com.sprint.mission.discodeit.message;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import com.sprint.mission.discodeit.global.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.message.dto.MessageCreateRequestDto;
import com.sprint.mission.discodeit.message.dto.MessageDto;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.mapper.MessageMapper;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.message.service.MessageService;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository binaryContentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private MessageService messageService;


    @Test
    @DisplayName("메시지 생성 성공")
    void messageCreate_success() {
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        MessageCreateRequestDto request = new MessageCreateRequestDto(userId, channelId, "메시지입니다");
        given(userRepository.findById(userId)).willReturn(Optional.of(mock(User.class)));
        given(channelRepository.findById(channelId)).willReturn(Optional.of(mock(Channel.class)));

        Message message = new Message(mock(User.class), mock(Channel.class), "메시지입니다", List.of());
        given(messageRepository.save(any(Message.class))).willReturn(message);
        given(messageMapper.toDto(message))
            .willReturn(new MessageDto(UUID.randomUUID(), null, null,
                "메시지입니다", channelId, null, List.of()));

        MessageDto result = messageService.messageCreate(request, List.of());

        assertThat(result.content()).isEqualTo("메시지입니다");
        then(messageRepository).should().save(any(Message.class));
    }

    @Test
    @DisplayName("메시지 생성 실패 - 유저 조회 실패")
    void messageCreate_fail_notFoundUser() {
        UUID userId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        MessageCreateRequestDto request = new MessageCreateRequestDto(userId, channelId, "메시지입니다");
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        DiscodeitException exception = assertThrows(DiscodeitException.class,
            () -> messageService.messageCreate(request, List.of()));

        assertThat(exception.getType()).isEqualTo(ExceptionType.USER_NOT_FOUND);
        then(messageRepository).should(never()).save(any());
    }
}
