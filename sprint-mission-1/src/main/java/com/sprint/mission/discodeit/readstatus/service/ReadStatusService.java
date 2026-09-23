package com.sprint.mission.discodeit.readstatus.service;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import com.sprint.mission.discodeit.readstatus.dto.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.readstatus.dto.ReadStatusDto;
import com.sprint.mission.discodeit.readstatus.dto.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadStatusService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ReadStatusMapper readStatusMapper;

    @Transactional
    public ReadStatusDto readStatusCreate(
        ReadStatusCreateRequestDto readStatusCreateRequestDto) {
        Channel channel = channelRepository.findById(readStatusCreateRequestDto.channelId())
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.CHANNEL_NOT_FOUND,
                Map.of("channelId", readStatusCreateRequestDto.channelId())
            ));

        User user = userRepository.findById(readStatusCreateRequestDto.userId())
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_NOT_FOUND,
                Map.of("userId", readStatusCreateRequestDto.userId())
            ));

        if (readStatusRepository.existsByUserAndChannel(user, channel)) {
            throw new DiscodeitException(
                ExceptionType.READ_STATUS_CONFLICT);
        }

        Instant lastReadAt = readStatusCreateRequestDto.lastReadAt() != null
            ? readStatusCreateRequestDto.lastReadAt()
            : Instant.now();

        return readStatusMapper.toDto(readStatusRepository.save(
            new ReadStatus(channel, user, lastReadAt)));
    }

    @Transactional
    public ReadStatusDto readStatusUpdate(UUID readStatusId,
        ReadStatusUpdateRequestDto readStatusUpdateRequestDto) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_STATUS_NOT_FOUND,
                Map.of("readStatusId", readStatusId)
            ));

        if (readStatusUpdateRequestDto.newLastReadAt() != null) {
            readStatus.updateAt(readStatusUpdateRequestDto.newLastReadAt());
        }

        return readStatusMapper.toDto(readStatus);
    }

    @Transactional
    public void readStatusDelete(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_STATUS_NOT_FOUND,
                Map.of("readStatusId", readStatusId)
            ));
        readStatusRepository.delete(readStatus);
    }

    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_NOT_FOUND,
                Map.of("userId", userId)
            ));

        List<ReadStatus> readStatuses = readStatusRepository.findByUser(user);

        return readStatuses.stream()
            .map(readStatusMapper::toDto)
            .toList();
    }

    public ReadStatusDto findReadStatus(UUID readStatusId) {
        return readStatusMapper.toDto(readStatusRepository.findById(readStatusId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_STATUS_NOT_FOUND,
                Map.of("readStatusId", readStatusId)
            )));
    }
}
