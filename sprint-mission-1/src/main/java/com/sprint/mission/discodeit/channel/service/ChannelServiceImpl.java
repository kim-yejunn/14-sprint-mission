package com.sprint.mission.discodeit.channel.service;

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

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public ChannelResponse channelCreate(
        ChannelPublicCreateRequestDto channelPublicCreateRequestDto) {
        Channel channel = new Channel(channelPublicCreateRequestDto.name(),
            ChannelType.PUBLIC, channelPublicCreateRequestDto.description());
        channelRepository.channelAdd(channel);
        return ChannelResponse.from(channel);
    }

    @Override
    public ChannelResponse privateChannelCreate(
        ChannelPrivateCreateRequestDto channelPrivateCreateRequestDto) {
        Channel channel = new Channel(ChannelType.PRIVATE);

        channelRepository.channelAdd(channel);

        for (UUID userId : channelPrivateCreateRequestDto.participantIds()) {
            User user = userRepository.findByUser(userId)
                .orElseThrow(() -> new DiscodeitException(
                    ExceptionType.USER_NOT_FOUND,
                    Map.of("userId", userId)
                ));

            ReadStatus readStatus = new ReadStatus(channel.getId(), user.getId());
            readStatusRepository.statusAdd(readStatus);
        }

        return ChannelResponse.from(channel);
    }

    @Override
    public ChannelResponse channelUpdate(UUID channelId,
        ChannelUpdateRequestDto channelUpdateRequestDto) {
        Channel channel = channelRepository.findByChannel(channelId)
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
        channelRepository.update(channel);

        return ChannelResponse.from(channel);
    }

    @Override
    public ChannelDto findById(UUID channelId) {
        Channel channel = channelRepository.findByChannel(channelId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.CHANNEL_NOT_FOUND,
                Map.of("channelId", channelId)
            ));
        return toResponseDto(channel);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<Channel> publicChannels = channelRepository.findAllByType(ChannelType.PUBLIC);

        List<UUID> myChannelIds = readStatusRepository.findByUserId(userId);
        List<Channel> myPrivateChannels = channelRepository.findAllByType(ChannelType.PRIVATE)
            .stream()
            .filter(channel -> myChannelIds.contains(channel.getId()))
            .toList();

        return Stream.concat(publicChannels.stream(), myPrivateChannels.stream())
            .map(this::toResponseDto)
            .toList();
    }

    // 채널 -> DTO로 변환
    // find랑 findAll이랑 겹쳐서 통합 사용을 위해 생성
    private ChannelDto toResponseDto(Channel channel) {
        List<Message> messages = messageRepository.findAllMessage(channel.getId());

        Instant lastMessageAt = messages.stream()
            .map(Message::getCreatedAt)
            .max(Instant::compareTo)
            .orElse(null);

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            List<UUID> participantIds = readStatusRepository.findByChannelId(
                channel.getId());
            return ChannelDto.from(channel, lastMessageAt, participantIds);
        }

        return ChannelDto.from(channel, lastMessageAt);
    }

    @Override
    public void channelDelete(UUID channelId) {
        Channel channel = channelRepository.findByChannel(channelId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.CHANNEL_NOT_FOUND,
                Map.of("channelId", channelId)
            ));

        List<UUID> attachmentIds = messageRepository.findAllMessage(channelId).stream()
            .map(Message::getAttachmentIds)
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .toList();

        attachmentIds.forEach(binaryContentRepository::delete);
        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteByChannelId(channelId);
        channelRepository.delete(channel);
    }
}
