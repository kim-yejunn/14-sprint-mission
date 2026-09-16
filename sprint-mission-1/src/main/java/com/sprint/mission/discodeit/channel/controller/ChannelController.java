package com.sprint.mission.discodeit.channel.controller;

import com.sprint.mission.discodeit.channel.dto.ChannelDto;
import com.sprint.mission.discodeit.channel.dto.ChannelPrivateCreateRequestDto;
import com.sprint.mission.discodeit.channel.dto.ChannelPublicCreateRequestDto;
import com.sprint.mission.discodeit.channel.dto.ChannelResponse;
import com.sprint.mission.discodeit.channel.dto.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.channel.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Channel", description = "Channel Api")
public class ChannelController {

    private final ChannelService channelService;

    @Operation(summary = "공개 채널 생성")
    @RequestMapping(method = RequestMethod.POST, value = "/api/channels/public")
    public ResponseEntity<ChannelResponse> publicCreate(
        @Valid @RequestBody ChannelPublicCreateRequestDto channelPublicCreateRequestDto) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(channelService.channelCreate(channelPublicCreateRequestDto));
    }

    @Operation(summary = "비공개 채널 생성")
    @RequestMapping(method = RequestMethod.POST, value = "/api/channels/private")
    public ResponseEntity<ChannelResponse> privateCreate(
        @Valid @RequestBody ChannelPrivateCreateRequestDto channelPrivateCreateRequestDto) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(channelService.privateChannelCreate(channelPrivateCreateRequestDto));
    }

    @Operation(summary = "공개 채널 수정")
    @RequestMapping(method = RequestMethod.PATCH, value = "/api/channels/{channelId}")
    public ResponseEntity<ChannelResponse> update(
        @PathVariable UUID channelId,
        @Valid @RequestBody ChannelUpdateRequestDto channelUpdateRequestDto) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(channelService.channelUpdate(channelId, channelUpdateRequestDto));
    }

    @Operation(summary = "채널 삭제")
    @RequestMapping(method = RequestMethod.DELETE, value = "/api/channels/{channelId}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID channelId) {
        channelService.channelDelete(channelId);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @Operation(summary = "특정 사용자의 채널 목록")
    @RequestMapping(method = RequestMethod.GET, value = "/api/channels")
    public ResponseEntity<List<ChannelDto>> findAllByUserId(
        @RequestParam UUID userId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(channelService.findAllByUserId(userId));
    }
}
