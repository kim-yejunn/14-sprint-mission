package com.sprint.mission.discodeit.binarycontent.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class BinaryContent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id = UUID.randomUUID();
    private final Instant createdAt = Instant.now();

    private final String fileName;
    private final String contentType;
    private final byte[] bytes;

    private final long size;

    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
        this.size = (bytes == null) ? 0 : bytes.length;
    }
}
