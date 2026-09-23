package com.sprint.mission.discodeit.binarycontent.storage;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentDto;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

    private final Path root;

    public LocalBinaryContentStorage(
        @Value("${discodeit.storage.local.root-path}") Path root
    ) {
        this.root = root;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException("저장소 디렉터리 생성에 실패했습니다: " + root, e);
        }
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        Path path = resolvePath(binaryContentId);
        try {
            Files.write(path, bytes);
        } catch (IOException e) {
            throw new DiscodeitException(
                ExceptionType.FILE_SAVE_FAILED,
                Map.of("binaryContentId", binaryContentId),
                e);
        }
        return binaryContentId;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        Path path = resolvePath(binaryContentId);
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new DiscodeitException(
                ExceptionType.FILE_NOT_FOUND,
                Map.of("binaryContentId", binaryContentId),
                e);
        }
    }

    @Override
    public ResponseEntity<Resource> download(BinaryContentDto dto) {
        Resource resource = new InputStreamResource(get(dto.id()));

        MediaType mediaType = dto.contentType() == null
            ? MediaType.APPLICATION_OCTET_STREAM
            : MediaType.parseMediaType(dto.contentType());

        ContentDisposition disposition = ContentDisposition.inline()
            .filename(dto.fileName(), StandardCharsets.UTF_8)
            .build();

        ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
            .contentType(mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString());

        if (dto.size() != null) {
            builder.contentLength(dto.size());
        }

        return builder.body(resource);
    }

    private Path resolvePath(UUID id) {
        return root.resolve(id.toString());
    }
}
