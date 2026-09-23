package com.sprint.mission.discodeit.binarycontent.service;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentDto;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Transactional
    public BinaryContentDto binaryContentCreate(
        BinaryContentCreateRequestDto binaryContentCreateRequestDto) {
        return binaryContentMapper.toDto(binaryContentRepository.save(
            new BinaryContent(binaryContentCreateRequestDto.fileName(),
                binaryContentCreateRequestDto.contentType(),
                binaryContentCreateRequestDto.bytes())));
    }

    @Transactional
    public void binaryContentDelete(UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.BINARY_CONTENT_NOT_FOUND,
                Map.of("binaryContentId", binaryContentId)
            ));
        binaryContentRepository.delete(binaryContent);
    }

    public List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentRepository.findAllById(binaryContentIds);

        return binaryContents.stream()
            .map(binaryContentMapper::toDto)
            .toList();
    }

    public BinaryContentDto findBinaryContent(UUID binaryContentId) {
        return binaryContentMapper.toDto(binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.BINARY_CONTENT_NOT_FOUND,
                Map.of("binaryContentId", binaryContentId)
            )));
    }
}
