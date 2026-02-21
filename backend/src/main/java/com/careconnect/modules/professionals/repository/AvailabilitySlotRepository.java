package com.careconnect.modules.professionals.repository;

import com.careconnect.modules.professionals.entity.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, UUID> {
    List<AvailabilitySlot> findByProfessionalIdAndBookedFalseOrderByStartTimeAsc(UUID professionalId);
}
