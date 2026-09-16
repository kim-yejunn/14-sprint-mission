package com.sprint.mission.discodeit.user.controller;

import com.sprint.mission.discodeit.user.dto.UserCreateRequestDto;
import com.sprint.mission.discodeit.user.dto.UserDto;
import com.sprint.mission.discodeit.user.dto.UserResponse;
import com.sprint.mission.discodeit.user.dto.UserUpdateRequestDto;
import com.sprint.mission.discodeit.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@RestController
@Tag(name = "User", description = "User Api")
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 생성")
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/api/users",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> create(
        @Valid @RequestPart(value = "userCreateRequest") UserCreateRequestDto userCreateRequestDto,
        @RequestPart(value = "profile", required = false) MultipartFile profile) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(userService.userCreate(userCreateRequestDto, profile));
    }

    @Operation(summary = "사용자 수정")
    @RequestMapping(method = RequestMethod.PATCH,
        value = "/api/users/{userId}",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> update(
        @Parameter(description = "사용자 ID")
        @PathVariable UUID userId,
        @Valid @RequestPart(value = "userUpdateRequest") UserUpdateRequestDto userUpdateRequestDto,
        @RequestPart(value = "profile", required = false) MultipartFile profile) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.userUpdate(userId, userUpdateRequestDto, profile));
    }

    @Operation(summary = "사용자 삭제")
    @RequestMapping(method = RequestMethod.DELETE, value = "/api/users/{userId}")
    public ResponseEntity<Void> delete(
        @Parameter(description = "사용자 ID")
        @PathVariable UUID userId) {
        userService.userDelete(userId);

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    @Operation(summary = "전체 사용자 조회")
    @RequestMapping(method = RequestMethod.GET, value = "/api/users")
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.findAll());
    }
}
