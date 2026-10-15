package com.sprint.mission.discodeit.channel;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.channel.dto.ChannelDto;
import com.sprint.mission.discodeit.channel.dto.ChannelPrivateCreateRequestDto;
import com.sprint.mission.discodeit.channel.dto.ChannelPublicCreateRequestDto;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.channel.mapper.ChannelMapper;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.channel.service.ChannelService;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
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
public class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private ChannelMapper channelMapper;

    @InjectMocks
    private ChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성 성공")
    void channelCreate_public_success() {
        ChannelPublicCreateRequestDto request =
            new ChannelPublicCreateRequestDto("공지", "공지 채널입니다");
        given(messageRepository.findLastMessageAt(any(Channel.class))).willReturn(null);
        given(channelMapper.toDto(any(Channel.class), eq(List.of()), any()))
            .willReturn(
                new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "공지", "공지 채널입니다", List.of(),
                    null));

        ChannelDto result = channelService.channelCreate(request);

        assertThat(result.name()).isEqualTo("공지");
        then(channelRepository).should().save(any(Channel.class));
    }

    @Test
    @DisplayName("비공개 채널 생성 성공")
    void channelCreate_private_success() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        ChannelPrivateCreateRequestDto request =
            new ChannelPrivateCreateRequestDto(List.of(userId1, userId2));
        given(userRepository.findById(userId1)).willReturn(Optional.of(mock(User.class)));
        given(userRepository.findById(userId2)).willReturn(Optional.of(mock(User.class)));
        given(channelMapper.toDto(any(Channel.class), anyList(), isNull()))
            .willReturn(
                new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, null, null, List.of(),
                    null));

        ChannelDto result = channelService.privateChannelCreate(request);

        then(readStatusRepository).should(times(2)).save(any(ReadStatus.class));
    }
}
