package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.repository.ChannelRepository;
import com.sprint.mission.discodeit.global.exception.DiscodeitException;
import com.sprint.mission.discodeit.global.exception.ExceptionType;
import com.sprint.mission.discodeit.message.dto.MessageCreateRequestDto;
import com.sprint.mission.discodeit.message.dto.MessageResponseDto;
import com.sprint.mission.discodeit.message.dto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.repository.MessageRepository;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public MessageResponseDto messageCreate(MessageCreateRequestDto messageCreateRequestDto,
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
        // TODO: BinaryContent 생성로직 변경

        Message message = new Message(user, channel, messageCreateRequestDto.content(),
            binaryContents);

        return MessageResponseDto.from(messageRepository.save(message));
    }

    @Transactional
    public MessageResponseDto messageUpdate(UUID messageId,
        MessageUpdateRequestDto messageUpdateRequestDto) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.MESSAGE_NOT_FOUND,
                Map.of("messageId", messageId)
            ));

        message.updateMessage(messageUpdateRequestDto.newContent());
        messageRepository.save(message);

        return MessageResponseDto.from(message);
    }

    @Transactional
    public void messageDelete(UUID messageId) {
        Message messages = messageRepository.findById(messageId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.MESSAGE_NOT_FOUND,
                Map.of("messageId", messageId)
            ));

        binaryContentRepository.deleteAll(messages.getAttachments());

        messageRepository.delete(messages);
    }

    public List<MessageResponseDto> findAllByChannelId(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.CHANNEL_NOT_FOUND,
                Map.of("channelId", channelId)
            ));
        return messageRepository.findAllByChannel(channel).stream()
            .map(MessageResponseDto::from)
            .toList();
    }

    public MessageResponseDto findById(UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new DiscodeitException(
                ExceptionType.MESSAGE_NOT_FOUND,
                Map.of("messageId", messageId)
            ));

        return MessageResponseDto.from(message);
    }
}
