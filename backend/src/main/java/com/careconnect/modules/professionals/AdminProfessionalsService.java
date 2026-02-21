package com.careconnect.modules.professionals;

import com.careconnect.modules.auth.entity.User;
import com.careconnect.modules.auth.entity.UserRole;
import com.careconnect.modules.auth.repository.UserRepository;
import com.careconnect.modules.professionals.dto.*;
import com.careconnect.modules.professionals.entity.Professional;
import com.careconnect.modules.professionals.repository.ProfessionalRepository;
import com.careconnect.shared.dto.PagedResponse;
import com.careconnect.shared.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminProfessionalsService {

    private final ProfessionalRepository professionalRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public PagedResponse<ProfessionalAdminResponse> listAll(int page, int size) {
        if (size > 50) size = 50;
        if (size < 1) size = 1;
        if (page < 0) throw AppException.badRequest("page must not be negative");

        var result = professionalRepository.findAll(
            Specification.where(null),
            PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return PagedResponse.from(result.map(this::toAdminResponse));
    }

    @Transactional
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ProfessionalAdminResponse create(CreateProfessionalRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw AppException.badRequest("Email already registered");
        }
        var user = User.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .phone(request.phone())
            .role(UserRole.PROFESSIONAL)
            .build();
        userRepository.save(user);

        var professional = Professional.builder()
            .user(user)
            .specialization(request.specialization())
            .licenseNumber(request.licenseNumber())
            .city(request.city())
            .hourlyRate(request.hourlyRate())
            .bio(request.bio())
            .verified(false)
            .build();
        professionalRepository.save(professional);
        return toAdminResponse(professional);
    }

    @Transactional
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ProfessionalAdminResponse update(UUID id, UpdateProfessionalRequest request) {
        var professional = professionalRepository.findById(id)
            .orElseThrow(() -> AppException.notFound("Professional not found"));

        if (request.specialization() != null) professional.setSpecialization(request.specialization());
        if (request.bio() != null) professional.setBio(request.bio());
        if (request.city() != null) professional.setCity(request.city());
        if (request.hourlyRate() != null) professional.setHourlyRate(request.hourlyRate());

        professionalRepository.save(professional);
        return toAdminResponse(professional);
    }

    @Transactional
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public void delete(UUID id) {
        var professional = professionalRepository.findById(id)
            .orElseThrow(() -> AppException.notFound("Professional not found"));
        professionalRepository.delete(professional);
    }

    @Transactional
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ProfessionalAdminResponse toggleVerify(UUID id) {
        var professional = professionalRepository.findById(id)
            .orElseThrow(() -> AppException.notFound("Professional not found"));
        professional.setVerified(!professional.isVerified());
        professionalRepository.save(professional);
        return toAdminResponse(professional);
    }

    private ProfessionalAdminResponse toAdminResponse(Professional p) {
        return new ProfessionalAdminResponse(
            p.getId(),
            p.getUser().getFirstName(),
            p.getUser().getLastName(),
            p.getUser().getEmail(),
            p.getUser().getPhone(),
            p.getSpecialization(),
            p.getLicenseNumber(),
            p.getBio(),
            p.getCity(),
            p.getHourlyRate(),
            p.isVerified(),
            p.getCreatedAt()
        );
    }
}
