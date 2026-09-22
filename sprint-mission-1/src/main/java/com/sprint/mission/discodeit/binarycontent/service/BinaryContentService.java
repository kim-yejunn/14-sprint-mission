package com.sprint.mission.discodeit.binarycontent.service;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
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

    @Transactional
    public BinaryContentResponseDto binaryContentCreate(
        BinaryContentCreateRequestDto binaryContentCreateRequestDto) {
        return BinaryContentResponseDto.from(binaryContentRepository.save(
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

    public List<BinaryContentResponseDto> findAllByIdIn(List<UUID> binaryContentIds) {
        // TODO: 파라미터 개선 사항
        List<BinaryContent> binaryContents = binaryContentRepository.findAll();

        return binaryContents.stream()
            .map(BinaryContentResponseDto::from)
            .toList();
    }

    public BinaryContentResponseDto findBinaryContent(UUID binaryContentId) {
        return BinaryContentResponseDto.from(binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.BINARY_CONTENT_NOT_FOUND,
                Map.of("binaryContentId", binaryContentId)
            )));
    }
}
