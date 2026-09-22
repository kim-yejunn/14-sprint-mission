package com.sprint.mission.discodeit.channel.entity;

import com.sprint.mission.discodeit.global.entity.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {

    private String name;
    private ChannelType type = ChannelType.PUBLIC;
    private String description;

    public Channel(ChannelType type) {
        this.type = type;
    }

    public Channel(String name, ChannelType type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }


    public void update(String channelName, String description) {
        boolean changed = false;
        if (channelName != null && !channelName.equals(this.name)) {
            this.name = channelName;
            changed = true;
        }
        if (description != null && !description.equals(this.description)) {
            this.description = description;
            changed = true;
        }
        if (changed) {
            super.markUpdated();
        }
    }
}
