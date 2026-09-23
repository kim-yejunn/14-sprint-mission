package com.sprint.mission.discodeit.binarycontent.controller;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentDto;
import com.sprint.mission.discodeit.binarycontent.service.BinaryContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "BinaryContent", description = "BinaryContent Api")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @Operation(summary = "파일 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/api/binaryContents/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> findById(
        @PathVariable UUID binaryContentId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(binaryContentService.findBinaryContent(binaryContentId));
    }

    @Operation(summary = "여러 파일 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/api/binaryContents")
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
        @RequestParam List<UUID> binaryContentIds) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(binaryContentService.findAllByIdIn(binaryContentIds));
    }
}
