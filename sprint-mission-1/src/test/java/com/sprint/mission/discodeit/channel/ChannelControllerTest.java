package com.sprint.mission.discodeit.channel;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.channel.controller.ChannelController;
import com.sprint.mission.discodeit.channel.dto.ChannelDto;
import com.sprint.mission.discodeit.channel.dto.ChannelPublicCreateRequestDto;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.channel.service.ChannelService;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성 성공")
    void publicCreate_success() throws Exception {
        ChannelPublicCreateRequestDto request =
            new ChannelPublicCreateRequestDto("공지", "공지 채널입니다");

        ChannelDto responseDto = new ChannelDto(
            UUID.randomUUID(), ChannelType.PUBLIC, "공지", "공지 채널입니다", List.of(), null);
        given(channelService.channelCreate(any())).willReturn(responseDto);

        mockMvc.perform(post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("공지"));
    }

    @Test
    @DisplayName("채널 삭제 실패")
    void delete_fail_notFound() throws Exception {
        UUID channelId = UUID.randomUUID();

        willThrow(new DiscodeitException(
            ExceptionType.CHANNEL_NOT_FOUND, Map.of("channelId", channelId)))
            .given(channelService).channelDelete(channelId);

        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").exists());
    }
}
