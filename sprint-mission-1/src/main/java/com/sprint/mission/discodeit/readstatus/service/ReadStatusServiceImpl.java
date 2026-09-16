package com.sprint.mission.discodeit.readstatus.service;

import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import com.sprint.mission.discodeit.readstatus.dto.ReadStatusCreateRequestDto;
import com.sprint.mission.discodeit.readstatus.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.readstatus.dto.ReadStatusUpdateRequestDto;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadStatusServiceImpl implements ReadStatusService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ReadStatusResponseDto readStatusCreate(
        ReadStatusCreateRequestDto readStatusCreateRequestDto) {
        channelRepository.findByChannel(readStatusCreateRequestDto.channelId())
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.CHANNEL_NOT_FOUND,
                Map.of("channelId", readStatusCreateRequestDto.channelId())
            ));

        userRepository.findByUser(readStatusCreateRequestDto.userId())
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_NOT_FOUND,
                Map.of("userId", readStatusCreateRequestDto.userId())
            ));

        Instant lastReadAt = readStatusCreateRequestDto.lastReadAt() != null
            ? readStatusCreateRequestDto.lastReadAt()
            : Instant.now();

        return ReadStatusResponseDto.from(readStatusRepository.statusAdd(
            new ReadStatus(readStatusCreateRequestDto.channelId(),
                readStatusCreateRequestDto.userId(),
                lastReadAt)));
    }

    @Override
    public ReadStatusResponseDto readStatusUpdate(UUID readStatusId,
        ReadStatusUpdateRequestDto readStatusUpdateRequestDto) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId);

        if (readStatusUpdateRequestDto.newLastReadAt() != null) {
            readStatus.updateAt(readStatusUpdateRequestDto.newLastReadAt());
        }

        readStatusRepository.update(readStatus);

        return ReadStatusResponseDto.from(readStatus);
    }

    @Override
    public void readStatusDelete(UUID readStatusId) {
        readStatusRepository.delete(readStatusId);
    }

    @Override
    public List<ReadStatusResponseDto> findAllByUserId(UUID userId) {
        List<ReadStatus> readStatuses = readStatusRepository.findByAllUserList(userId);

        return readStatuses.stream()
            .map(ReadStatusResponseDto::from)
            .toList();
    }

    @Override
    public ReadStatusResponseDto findReadStatus(UUID readStatusId) {
        return ReadStatusResponseDto.from(readStatusRepository.findById(readStatusId));
    }
}
