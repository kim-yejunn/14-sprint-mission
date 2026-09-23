package com.sprint.mission.discodeit.binarycontent.controller;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentDto;
import com.sprint.mission.discodeit.binarycontent.service.BinaryContentService;
import com.sprint.mission.discodeit.binarycontent.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/binaryContents")
@Tag(name = "BinaryContent", description = "BinaryContent Api")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @Operation(summary = "파일 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> findById(
        @PathVariable UUID binaryContentId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(binaryContentService.findBinaryContent(binaryContentId));
    }

    @Operation(summary = "여러 파일 조회")
    @RequestMapping(method = RequestMethod.GET, value = "")
    public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
        @RequestParam List<UUID> binaryContentIds) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(binaryContentService.findAllByIdIn(binaryContentIds));
    }

    @Operation
    @GetMapping(value = "/{binaryContentId}/download")
    public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
        return binaryContentStorage.download(
            binaryContentService.findBinaryContent(binaryContentId));
    }
}
