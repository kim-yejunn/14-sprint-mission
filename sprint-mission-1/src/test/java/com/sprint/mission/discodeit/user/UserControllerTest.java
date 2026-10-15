package com.sprint.mission.discodeit.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import com.sprint.mission.discodeit.user.controller.UserController;
import com.sprint.mission.discodeit.user.dto.UserDto;
import com.sprint.mission.discodeit.user.service.UserService;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("사용자 생성 성공")
    void create_success() throws Exception {
        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest", "", "application/json",
            "{\"username\":\"kyj\",\"password\":\"pw1234\",\"email\":\"a@b.com\"}".getBytes());

        UserDto responseDto = new UserDto(
            UUID.randomUUID(), "kyj", "a@b.com", null, false);
        given(userService.userCreate(any(), any())).willReturn(responseDto);

        mockMvc.perform(multipart("/api/users").file(requestPart))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("kyj"));
    }

    @Test
    @DisplayName("사용자 삭제 실패")
    void delete_fail_notFound() throws Exception {
        UUID userId = UUID.randomUUID();
        willThrow(new DiscodeitException(ExceptionType.USER_NOT_FOUND, Map.of("userId", userId)))
            .given(userService).userDelete(userId);

        mockMvc.perform(delete("/api/users/{userId}", userId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").exists());
    }
}