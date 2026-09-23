package com.sprint.mission.discodeit.channel.service;

import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.dto.ChannelDto;
import com.sprint.mission.discodeit.channel.dto.ChannelPrivateCreateRequestDto;
import com.sprint.mission.discodeit.channel.dto.ChannelPublicCreateRequestDto;
import com.sprint.mission.discodeit.channel.dto.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.channel.mapper.ChannelMapper;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelMapper channelMapper;

    @Transactional
    public ChannelDto channelCreate(
        ChannelPublicCreateRequestDto channelPublicCreateRequestDto) {
        Channel channel = new Channel(channelPublicCreateRequestDto.name(),
            ChannelType.PUBLIC, channelPublicCreateRequestDto.description());
        channelRepository.save(channel);
        return toResponseDto(channel);
    }

    @Transactional
    public ChannelDto privateChannelCreate(
        ChannelPrivateCreateRequestDto channelPrivateCreateRequestDto) {
        Channel channel = new Channel(ChannelType.PRIVATE);

        channelRepository.save(channel);

        List<User> participants = new ArrayList<>();

        for (UUID userId : channelPrivateCreateRequestDto.participantIds()) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new DiscodeitException(
                    ExceptionType.USER_NOT_FOUND,
                    Map.of("userId", userId)
                ));

            readStatusRepository.save(new ReadStatus(channel, user));
            participants.add(user);
        }

        return channelMapper.toDto(channel, participants, null);
    }

    @Transactional
    public ChannelDto channelUpdate(UUID channelId,
        ChannelUpdateRequestDto channelUpdateRequestDto) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.CHANNEL_NOT_FOUND,
                Map.of("channelId", channelId)
            ));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new DiscodeitException(
                ExceptionType.PRIVATE_CHANNEL_UPDATE_DENIED,
                Map.of("channelId", channelId)
            );
        }

        channel.update(channelUpdateRequestDto.newName(),
            channelUpdateRequestDto.newDescription());

        return toResponseDto(channel);
    }

    public ChannelDto findById(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.CHANNEL_NOT_FOUND,
                Map.of("channelId", channelId)
            ));
        return toResponseDto(channel);
    }

    public List<ChannelDto> findAllByUserId(UUID userId) {
        return channelRepository.findAllAccessible(userId).stream()
            .map(this::toResponseDto)
            .toList();
    }

    @Transactional
    public void channelDelete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.CHANNEL_NOT_FOUND,
                Map.of("channelId", channelId)
            ));

        readStatusRepository.deleteByChannel(channel);
        channelRepository.delete(channel);
    }

    private ChannelDto toResponseDto(Channel channel) {
        Instant lastMessageAt = messageRepository.findLastMessageAt(channel);

        List<User> participants = channel.getType() == ChannelType.PRIVATE
            ? readStatusRepository.findByChannel(channel).stream()
            .map(ReadStatus::getUser)
            .toList()
            : List.of();

        return channelMapper.toDto(channel, participants, lastMessageAt);
    }
}
