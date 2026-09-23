package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.binarycontent.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.global.dto.PageResponse;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import com.sprint.mission.discodeit.global.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.message.dto.MessageCreateRequestDto;
import com.sprint.mission.discodeit.message.dto.MessageDto;
import com.sprint.mission.discodeit.message.dto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.mapper.MessageMapper;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    @Transactional
    public MessageDto messageCreate(MessageCreateRequestDto messageCreateRequestDto,
        List<MultipartFile> attachments) {
        User user = userRepository.findById(messageCreateRequestDto.authorId())
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.USER_NOT_FOUND,
                Map.of("authorId", messageCreateRequestDto.authorId())
            ));

        Channel channel = channelRepository.findById(messageCreateRequestDto.channelId())
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.CHANNEL_NOT_FOUND,
                Map.of("channelId", messageCreateRequestDto.channelId())
            ));

        List<BinaryContent> binaryContents = new ArrayList<>();

        if (attachments != null) {
            for (MultipartFile attachment : attachments) {
                BinaryContent binaryContent = binaryContentRepository.save(
                    new BinaryContent(
                        Objects.requireNonNull(attachment.getOriginalFilename()),
                        attachment.getContentType(), attachment.getSize()));
                binaryContents.add(binaryContent);
                try {
                    binaryContentStorage.put(binaryContent.getId(), attachment.getBytes());
                } catch (IOException e) {
                    throw new DiscodeitException(
                        ExceptionType.FILE_SAVE_FAILED,
                        Map.of("binaryContentId", binaryContent.getId()),
                        e);
                }
            }
        }

        Message message = new Message(user, channel, messageCreateRequestDto.content(),
            binaryContents);

        return messageMapper.toDto(messageRepository.save(message));
    }

    @Transactional
    public MessageDto messageUpdate(UUID messageId,
        MessageUpdateRequestDto messageUpdateRequestDto) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.MESSAGE_NOT_FOUND,
                Map.of("messageId", messageId)
            ));

        message.updateMessage(messageUpdateRequestDto.newContent());

        return messageMapper.toDto(message);
    }

    @Transactional
    public void messageDelete(UUID messageId) {
        Message messages = messageRepository.findById(messageId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.MESSAGE_NOT_FOUND,
                Map.of("messageId", messageId)
            ));

        messageRepository.delete(messages);
    }

    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.CHANNEL_NOT_FOUND,
                Map.of("channelId", channelId)
            ));

        Slice<Message> slice = messageRepository.findAllByChannel(channel, pageable);

        return pageResponseMapper.fromSlice(slice.map(messageMapper::toDto));
    }

    public MessageDto findById(UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.MESSAGE_NOT_FOUND,
                Map.of("messageId", messageId)
            ));

        return messageMapper.toDto(message);
    }
}
