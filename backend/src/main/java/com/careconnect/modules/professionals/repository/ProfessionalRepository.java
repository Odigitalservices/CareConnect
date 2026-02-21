package com.careconnect.modules.professionals.repository;

import com.careconnect.modules.professionals.entity.Professional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfessionalRepository
        extends JpaRepository<Professional, UUID>,
                JpaSpecificationExecutor<Professional> {

    @Override
    @EntityGraph(attributePaths = "user")
    Page<Professional> findAll(Specification<Professional> spec, Pageable pageable);

    List<Professional> findByCityAndVerifiedTrue(String city);
    Optional<Professional> findByUserId(UUID userId);
}
