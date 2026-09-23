package com.sprint.mission.discodeit.global.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseUpdatableEntity extends BaseEntity {

    @LastModifiedDate
    private Instant updatedAt;
    
    public void markUpdated() {
        this.updatedAt = Instant.now();
    }

    public void updateAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
