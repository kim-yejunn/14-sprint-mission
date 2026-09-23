package com.sprint.mission.discodeit.message.entity;


import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.global.entity.BaseUpdatableEntity;
import com.sprint.mission.discodeit.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @ManyToOne(fetch = FetchType.LAZY)
    private Channel channel;
    @NonNull
    private String content;
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinTable(
        name = "message_attachments",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments = new ArrayList<>();

    public Message(User author, Channel channel, String content,
        List<BinaryContent> attachments) {
        this.author = author;
        this.channel = channel;
        this.content = content;
        this.attachments = attachments;
    }

    public void updateMessage(String updateMessage) {
        if (updateMessage != null && !updateMessage.equals(this.content)) {
            this.content = updateMessage;
            super.markUpdated();
        }
    }
}
