package com.careconnect.modules.chat.repository;

import com.careconnect.modules.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByConversationIdOrderBySentAtAsc(UUID conversationId);
}
