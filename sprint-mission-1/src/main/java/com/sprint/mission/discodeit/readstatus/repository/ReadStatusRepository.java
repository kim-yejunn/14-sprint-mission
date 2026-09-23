package com.sprint.mission.discodeit.readstatus.repository;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.user.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    List<ReadStatus> findByUser(User user);

    List<ReadStatus> findByChannel(Channel channel);

    void deleteByChannel(Channel channel);

    void deleteAllByUser(User user);

    boolean existsByUserAndChannel(User user, Channel channel);

    @Query("""
        select rs from ReadStatus rs
        join fetch rs.user u
        left join fetch u.profile
        left join fetch u.status
        where rs.channel.id in :channelIds
        """)
    List<ReadStatus> findAllByChannelIdIn(@Param("channelIds") List<UUID> channelIds);
}
