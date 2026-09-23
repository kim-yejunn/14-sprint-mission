package com.sprint.mission.discodeit.channel.repository;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    List<Channel> findAllByType(ChannelType channelType);

    @Query("""
        select c from Channel c
        where c.type = com.sprint.mission.discodeit.channel.entity.ChannelType.PUBLIC
           or c.id in (select rs.channel.id from ReadStatus rs where rs.user.id = :userId)
        """)
    List<Channel> findAllAccessible(@Param("userId") UUID userId);
}
