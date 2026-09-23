package com.sprint.mission.discodeit.message.repository;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.user.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Slice<Message> findAllByChannel(Channel channel, Pageable pageable);

    @Modifying
    @Query("update Message m set m.author = null where m.author = :user")
    void clearAuthor(@Param("user") User user);

    @Query("""
        select m.channel.id, max(m.createdAt)
        from Message m
        where m.channel.id in :channelIds
        group by m.channel.id
        """)
    List<Object[]> findLastMessageAtByChannelIds(@Param("channelIds") List<UUID> channelIds);

    @Query("select max(m.createdAt) from Message m where m.channel = :channel")
    Instant findLastMessageAt(@Param("channel") Channel channel);
}
