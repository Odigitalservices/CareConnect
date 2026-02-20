package com.careconnect.modules.professionals.repository;

import com.careconnect.modules.professionals.entity.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfessionalRepository extends JpaRepository<Professional, UUID> {
    List<Professional> findByCityAndVerifiedTrue(String city);
    Optional<Professional> findByUserId(UUID userId);
}
