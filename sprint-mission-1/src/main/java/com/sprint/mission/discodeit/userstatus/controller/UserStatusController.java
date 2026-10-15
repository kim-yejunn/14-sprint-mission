package com.sprint.mission.discodeit.userstatus.controller;

import com.sprint.mission.discodeit.userstatus.dto.UserStatusDto;
import com.sprint.mission.discodeit.userstatus.dto.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.userstatus.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "UserStatus", description = "UserStatus Api")
public class UserStatusController {

    private final UserStatusService userStatusService;

    @Operation(summary = "사용자 상태 수정")
    @RequestMapping(method = RequestMethod.PATCH, value = "/api/users/{userId}/userStatus")
    public ResponseEntity<UserStatusDto> userStatusUpdate(
        @PathVariable UUID userId,
        @Valid @RequestBody UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userStatusService.userStatusUpdateByUserId(userId, userStatusUpdateRequestDto));
    }
}
