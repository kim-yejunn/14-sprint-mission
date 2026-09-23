package com.sprint.mission.discodeit.message.controller;

import com.sprint.mission.discodeit.message.dto.MessageCreateRequestDto;
import com.sprint.mission.discodeit.message.dto.MessageDto;
import com.sprint.mission.discodeit.message.dto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@Tag(name = "Message", description = "Message Api")
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "채널의 전체 메시지 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/api/messages")
    public ResponseEntity<List<MessageDto>> findAll(
        @RequestParam UUID channelId
    ) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(messageService.findAllByChannelId(channelId));
    }

    @Operation(summary = "메시지 생성")
    @RequestMapping(method = RequestMethod.POST,
        value = "/api/messages",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDto> create(
        @Valid @RequestPart(value = "messageCreateRequest") MessageCreateRequestDto messageCreateRequestDto,
        @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(messageService.messageCreate(messageCreateRequestDto, attachments));
    }

    @Operation(summary = "메시지 수정")
    @RequestMapping(method = RequestMethod.PATCH, value = "/api/messages/{messageId}")
    public ResponseEntity<MessageDto> update(
        @PathVariable UUID messageId,
        @Valid @RequestBody MessageUpdateRequestDto messageUpdateRequestDto) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(messageService.messageUpdate(messageId, messageUpdateRequestDto));
    }

    @Operation(summary = "메시지 삭제")
    @RequestMapping(method = RequestMethod.DELETE, value = "/api/messages/{messageId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID messageId) {
        messageService.messageDelete(messageId);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }
}
