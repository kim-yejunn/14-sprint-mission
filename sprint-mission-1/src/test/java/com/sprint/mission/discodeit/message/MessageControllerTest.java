package com.sprint.mission.discodeit.message;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import com.sprint.mission.discodeit.message.controller.MessageController;
import com.sprint.mission.discodeit.message.dto.MessageDto;
import com.sprint.mission.discodeit.message.service.MessageService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    @DisplayName("메시지 생성 성공")
    void create_success() throws Exception {
        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest", "", "application/json",
            ("{\"authorId\":\"" + UUID.randomUUID()
                + "\",\"channelId\":\"" + UUID.randomUUID()
                + "\",\"content\":\"메시지입니다\"}").getBytes());

        MessageDto responseDto = new MessageDto(
            UUID.randomUUID(), null, null, "메시지입니다", UUID.randomUUID(), null, List.of());
        given(messageService.messageCreate(any(), any())).willReturn(responseDto);

        mockMvc.perform(multipart("/api/messages").file(requestPart))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("메시지입니다"));
    }

    @Test
    @DisplayName("메시지 삭제 실패")
    void delete_fail_notFound() throws Exception {
        UUID messageId = UUID.randomUUID();

        willThrow(new DiscodeitException(
            ExceptionType.MESSAGE_NOT_FOUND, Map.of("messageId", messageId)))
            .given(messageService).messageDelete(messageId);

        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").exists());
    }
}
