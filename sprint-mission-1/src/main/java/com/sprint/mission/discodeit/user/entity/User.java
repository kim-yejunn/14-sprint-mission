package com.sprint.mission.discodeit.user.entity;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.global.entity.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

    private String username;
    private String password;
    private String email;
    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    private User(String username, String password, String email, BinaryContent profile) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.profile = profile;
    }

    private User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public static User create(String name, String password, String email, BinaryContent binaryId) {
        return new User(name, password, email, binaryId);
    }

    public static User create(String name, String password, String email) {
        return new User(name, password, email);
    }

    public void update(String name, String password, String email) {
        if (name != null) {
            this.username = name;
        }
        if (password != null) {
            this.password = password;
        }
        if (email != null) {
            this.email = email;
        }
        super.markUpdated();
    }

    public void updateProfile(BinaryContent binaryId) {
        if (binaryId != null) {
            this.profile = binaryId;
            markUpdated();
        }
    }
}
