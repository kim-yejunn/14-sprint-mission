package com.sprint.mission.discodeit.channel;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("접근 가능한 채널 조회 - PUBLIC + 참여중인 PRIVATE만")
    void findAllAccessible_success() {
        Channel publicChannel = channelRepository.save(new Channel("공지", ChannelType.PUBLIC, "설명"));
        Channel privateChannel = channelRepository.save(new Channel(ChannelType.PRIVATE));
        Channel privateChannel2 = channelRepository.save(new Channel(ChannelType.PRIVATE));

        User user = userRepository.save(User.create("kyj", "pw1234", "a@b.com", null));
        testEntityManager.persist(new ReadStatus(privateChannel, user));
        testEntityManager.flush();
        testEntityManager.clear();

        List<Channel> result = channelRepository.findAllAccessible(user.getId());

        assertThat(result).extracting(Channel::getId)
            .containsExactlyInAnyOrder(publicChannel.getId(), privateChannel.getId())
            .doesNotContain(privateChannel2.getId());
    }
}
