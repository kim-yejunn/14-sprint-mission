package com.sprint.mission.discodeit.message;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("최신 메시지 조회")
    void findLatest_success_hasNext() {
        Channel channel = channelRepository.save(new Channel("공지", ChannelType.PUBLIC, "설명"));
        for (int i = 0; i < 5; i++) {
            messageRepository.save(new Message(null, channel, "메시지" + i, List.of()));
            testEntityManager.flush();
        }
        testEntityManager.clear();

        List<Message> result = messageRepository.findLatest(channel.getId(), PageRequest.of(0, 5));

        assertThat(result).hasSize(5);
    }

    @Test
    @DisplayName("커서 이전 메시지 조회")
    void findBefore_success() {
        Channel channel = channelRepository.save(new Channel("공지", ChannelType.PUBLIC, "설명"));
        Instant cursor = null;
        for (int i = 0; i < 5; i++) {
            Message message = new Message(null, channel, "메시지" + i, List.of());
            messageRepository.save(message);
            if (i == 2) {
                cursor = message.getCreatedAt();
            }
            testEntityManager.flush();
        }
        testEntityManager.clear();

        List<Message> result = messageRepository.findBefore(channel.getId(), cursor,
            PageRequest.of(0, 5));

        assertThat(result).hasSize(2);
    }
}
