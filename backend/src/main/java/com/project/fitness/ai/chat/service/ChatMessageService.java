package com.project.fitness.ai.chat.service;

import com.project.fitness.ai.chat.entity.ChatMessage;
import com.project.fitness.ai.chat.entity.AiResponseStatus;
import com.project.fitness.ai.chat.entity.ChatMessageSender;
import com.project.fitness.ai.chat.entity.MessageType;
import com.project.fitness.ai.chat.repository.ChatMessageRepository;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {

  private final ChatMessageRepository repository;

  public ChatMessageService(ChatMessageRepository repository) {
    this.repository = repository;
  }

  public ChatMessage addMessage(Long sessionId, ChatMessageSender sender, MessageType type,
      String content, Integer tokenCount, AiResponseStatus responseStatus) {
    ChatMessage message = ChatMessage.builder()
        .sessionId(sessionId)
        .sender(sender)
        .messageType(type)
        .responseStatus(responseStatus)
        .content(content)
        .tokenCount(tokenCount)
        .build();
    return repository.save(message);
  }

  public Page<ChatMessage> listMessages(Long sessionId, Pageable pageable) {
    return repository.findBySessionIdOrderByCreatedAtDesc(sessionId, pageable);
  }

  public List<ChatMessage> recentMessages(Long sessionId, int maxMessages) {
    List<ChatMessage> messages = repository.findTop50BySessionIdOrderByCreatedAtDesc(sessionId);
    Collections.reverse(messages);
    if (messages.size() <= maxMessages) {
      return messages;
    }
    return messages.subList(messages.size() - maxMessages, messages.size());
  }

  public void updateMessageStatus(Long messageId, AiResponseStatus status) {
    repository.findById(messageId).ifPresent(message -> {
      message.setResponseStatus(status);
      repository.save(message);
    });
  }

  public void updateMessageContentAndStatus(Long messageId, String content, AiResponseStatus status) {
    repository.findById(messageId).ifPresent(message -> {
      message.setContent(content);
      message.setResponseStatus(status);
      repository.save(message);
    });
  }

  public long countMessages(Long sessionId) {
    return repository.countBySessionId(sessionId);
  }

  public List<ChatMessage> messagesAfter(Long sessionId, Long lastMessageId, int limit) {
    return repository.findBySessionIdAndIdGreaterThanOrderByIdAsc(
        sessionId,
        lastMessageId == null ? 0L : lastMessageId,
        org.springframework.data.domain.PageRequest.of(0, limit));
  }
}
