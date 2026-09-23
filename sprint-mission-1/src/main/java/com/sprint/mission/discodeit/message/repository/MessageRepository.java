package com.sprint.mission.discodeit.message.repository;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.user.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findAllByChannel(Channel channel);

    void deleteByChannel(Channel channel);

    @Query("select max(m.createdAt) from Message m where m.channel = :channel")
    Instant findLastMessageAt(@Param("channel") Channel channel);

    @Modifying
    @Query("update Message m set m.author = null where m.author = :user")
    void clearAuthor(@Param("user") User user);
}
