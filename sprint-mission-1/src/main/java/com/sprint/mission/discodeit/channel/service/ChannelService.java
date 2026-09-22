package com.sprint.mission.discodeit.channel.service;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.dto.ChannelDto;
import com.sprint.mission.discodeit.channel.dto.ChannelPrivateCreateRequestDto;
import com.sprint.mission.discodeit.channel.dto.ChannelPublicCreateRequestDto;
import com.sprint.mission.discodeit.channel.dto.ChannelResponse;
import com.sprint.mission.discodeit.channel.dto.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
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

    @Transactional
    public ChannelResponse channelCreate(
        ChannelPublicCreateRequestDto channelPublicCreateRequestDto) {
        Channel channel = new Channel(channelPublicCreateRequestDto.name(),
            ChannelType.PUBLIC, channelPublicCreateRequestDto.description());
        channelRepository.save(channel);
        return ChannelResponse.from(channel);
    }

    @Transactional
    public ChannelResponse privateChannelCreate(
        ChannelPrivateCreateRequestDto channelPrivateCreateRequestDto) {
        Channel channel = new Channel(ChannelType.PRIVATE);

        channelRepository.save(channel);

        for (UUID userId : channelPrivateCreateRequestDto.participantIds()) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new DiscodeitException(
                    ExceptionType.USER_NOT_FOUND,
                    Map.of("userId", userId)
                ));

            ReadStatus readStatus = new ReadStatus(channel, user);
            readStatusRepository.save(readStatus);
        }

        return ChannelResponse.from(channel);
    }

    @Transactional
    public ChannelResponse channelUpdate(UUID channelId,
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
        channelRepository.save(channel);

        return ChannelResponse.from(channel);
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
        List<Channel> publicChannels = channelRepository.findAllByType(ChannelType.PUBLIC);

        List<Channel> myPrivateChannels = channelRepository.findAllByType(ChannelType.PRIVATE);

        return Stream.concat(publicChannels.stream(), myPrivateChannels.stream())
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

        List<UUID> attachmentsId = messageRepository.findAllByChannel(channel).stream()
            .map(Message::getAttachments)
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .map(BinaryContent::getId)
            .toList();

        attachmentsId.forEach(binaryContentRepository::deleteById);
        messageRepository.deleteByChannel(channel);
        readStatusRepository.deleteByChannel(channel);
        channelRepository.delete(channel);
    }

    private ChannelDto toResponseDto(Channel channel) {
        List<Message> messages = messageRepository.findAllByChannel(channel);

        Instant lastMessageAt = messages.stream()
            .map(Message::getCreatedAt)
            .max(Instant::compareTo)
            .orElse(null);

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            List<UUID> participantIds = readStatusRepository.findByChannel((channel))
                .stream()
                .map(ReadStatus::getId)
                .toList();
            return ChannelDto.from(channel, lastMessageAt, participantIds);
        }

        return ChannelDto.from(channel, lastMessageAt);
    }
}
