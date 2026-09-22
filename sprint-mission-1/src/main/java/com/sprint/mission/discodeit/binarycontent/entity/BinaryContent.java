package com.sprint.mission.discodeit.binarycontent.entity;

import com.sprint.mission.discodeit.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseEntity {

    private String fileName;
    private String contentType;
    private byte[] bytes;
    private long size;

    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
        this.size = (bytes == null) ? 0 : bytes.length;
    }
}
