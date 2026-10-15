package com.sprint.mission.discodeit.binarycontent.storage;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentDto;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String bucket;
    private final long presignedUrlExpiration;
    private final S3Client s3Client;
    private final S3Presigner presigner;

    public S3BinaryContentStorage(
        @Value("${discodeit.storage.s3.access-key}") String accessKey,
        @Value("${discodeit.storage.s3.secret-key}") String secretKey,
        @Value("${discodeit.storage.s3.region}") String region,
        @Value("${discodeit.storage.s3.bucket}") String bucket,
        @Value("${discodeit.storage.s3.presigned-url-expiration}") long presignedUrlExpiration
    ) {
        StaticCredentialsProvider credentials = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey));
        this.bucket = bucket;
        this.presignedUrlExpiration = presignedUrlExpiration;
        this.s3Client = S3Client.builder().region(Region.of(region))
            .credentialsProvider(credentials).build();
        this.presigner = S3Presigner.builder().region(Region.of(region))
            .credentialsProvider(credentials).build();
    }

    @Override
    public UUID put(UUID binaryContentId, byte[] bytes) {
        try {
            s3Client.putObject(
                PutObjectRequest.builder().bucket(bucket).key(binaryContentId.toString()).build(),
                RequestBody.fromBytes(bytes));
        } catch (SdkException e) {
            throw new DiscodeitException(ExceptionType.FILE_SAVE_FAILED,
                Map.of("binaryContentId", binaryContentId), e);
        }
        log.info("S3 업로드 완료: binaryContentId={}", binaryContentId);
        return binaryContentId;
    }

    @Override
    public InputStream get(UUID binaryContentId) {
        try {
            return s3Client.getObject(
                GetObjectRequest.builder().bucket(bucket).key(binaryContentId.toString()).build());
        } catch (NoSuchKeyException e) {
            throw new DiscodeitException(ExceptionType.FILE_NOT_FOUND,
                Map.of("binaryContentId", binaryContentId), e);
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDto dto) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
            .bucket(bucket)
            .key(dto.id().toString())
            .responseContentType(dto.contentType())
            .responseContentDisposition(ContentDisposition.attachment()
                .filename(dto.fileName(), StandardCharsets.UTF_8).build().toString())
            .build();

        URL url = presigner.presignGetObject(GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
            .getObjectRequest(getRequest)
            .build()).url();

        log.info("Presigned URL 발급: binaryContentId={}", dto.id());
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(url.toString()))
            .build();
    }
}