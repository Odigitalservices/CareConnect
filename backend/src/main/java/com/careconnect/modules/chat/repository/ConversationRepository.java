package com.careconnect.modules.chat.repository;

import com.careconnect.modules.chat.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    List<Conversation> findByPatientId(UUID patientId);
    Optional<Conversation> findByPatientIdAndProfessionalId(UUID patientId, UUID professionalId);
}
