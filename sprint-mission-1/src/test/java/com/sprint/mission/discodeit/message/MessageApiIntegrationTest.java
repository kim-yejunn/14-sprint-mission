package com.sprint.mission.discodeit.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MessageApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private MessageRepository messageRepository;

    private User author;
    private Channel channel;

    @BeforeEach
    void setUp() {
        author = userRepository.save(User.create("kyj", "pw1234", "a@b.com", null));
        channel = channelRepository.save(new Channel("공지", ChannelType.PUBLIC, "설명"));
    }

    @Test
    @DisplayName("메시지 생성 성공")
    void createMessage_success() throws Exception {
        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest", "", "application/json",
            ("{\"authorId\":\"" + author.getId() + "\",\"channelId\":\"" + channel.getId()
                + "\",\"content\":\"안녕\"}").getBytes());

        mockMvc.perform(multipart("/api/messages").file(requestPart))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value("안녕"));

        assertThat(messageRepository.findAllByChannel(channel, PageRequest.of(0, 10))
            .getContent()).hasSize(1);
    }

    @Test
    @DisplayName("채널의 메시지 목록 조회 성공")
    void findAllByChannelId_success() throws Exception {
        messageRepository.save(new Message(author, channel, "메시지1", List.of()));

        mockMvc.perform(get("/api/messages")
                .param("channelId", channel.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1));
    }
}
