package com.careconnect.modules.professionals;

import com.careconnect.modules.professionals.dto.*;
import com.careconnect.modules.professionals.entity.AvailabilitySlot;
import com.careconnect.modules.professionals.entity.Professional;
import com.careconnect.modules.professionals.repository.AvailabilitySlotRepository;
import com.careconnect.modules.professionals.repository.ProfessionalRepository;
import com.careconnect.modules.professionals.repository.ProfessionalSpecifications;
import com.careconnect.shared.dto.PagedResponse;
import com.careconnect.shared.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfessionalsService {

    private static final int MAX_PAGE_SIZE = 50;

    private final ProfessionalRepository professionalRepository;
    private final AvailabilitySlotRepository availabilitySlotRepository;

    @Transactional(readOnly = true)
    public PagedResponse<ProfessionalSummaryResponse> listProfessionals(
            String city, String specialization,
            BigDecimal minRate, BigDecimal maxRate,
            int page, int size) {

        if (size > MAX_PAGE_SIZE) size = MAX_PAGE_SIZE;
        if (size < 1) size = 1;
        if (page < 0) throw AppException.badRequest("page must not be negative");
        if (minRate != null && maxRate != null && minRate.compareTo(maxRate) > 0) {
            throw AppException.badRequest("minRate cannot be greater than maxRate");
        }

        Specification<Professional> spec = Specification
            .where(ProfessionalSpecifications.isVerified())
            .and(ProfessionalSpecifications.hasCity(city))
            .and(ProfessionalSpecifications.hasSpecialization(specialization))
            .and(ProfessionalSpecifications.hasMinRate(minRate))
            .and(ProfessionalSpecifications.hasMaxRate(maxRate));

        var resultsPage = professionalRepository.findAll(spec, PageRequest.of(page, size));
        return PagedResponse.from(resultsPage.map(this::toSummary));
    }

    @Transactional(readOnly = true)
    public ProfessionalDetailResponse getProfessionalById(UUID id) {
        var professional = professionalRepository.findById(id)
            .filter(Professional::isVerified)
            .orElseThrow(() -> AppException.notFound("Professional not found"));

        var slots = availabilitySlotRepository
            .findByProfessionalIdAndBookedFalse(id)
            .stream().map(this::toSlotResponse).toList();

        return toDetail(professional, slots);
    }

    private ProfessionalSummaryResponse toSummary(Professional p) {
        return new ProfessionalSummaryResponse(
            p.getId(),
            p.getUser().getFirstName(),
            p.getUser().getLastName(),
            p.getSpecialization(),
            p.getCity(),
            p.getHourlyRate(),
            p.getCreatedAt()
        );
    }

    private ProfessionalDetailResponse toDetail(Professional p, List<AvailabilitySlotResponse> slots) {
        return new ProfessionalDetailResponse(
            p.getId(),
            p.getUser().getFirstName(),
            p.getUser().getLastName(),
            p.getUser().getEmail(),
            p.getUser().getPhone(),
            p.getSpecialization(),
            p.getBio(),
            p.getCity(),
            p.getHourlyRate(),
            slots,
            p.getCreatedAt()
        );
    }

    private AvailabilitySlotResponse toSlotResponse(AvailabilitySlot s) {
        return new AvailabilitySlotResponse(s.getId(), s.getStartTime(), s.getEndTime());
    }
}
