package com.careconnect.modules.professionals.repository;

import com.careconnect.modules.professionals.entity.Professional;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;

public class ProfessionalSpecifications {
    private ProfessionalSpecifications() {}

    // Always restricts to verified=true; omit this spec entirely if you don't want that restriction.
    public static Specification<Professional> isVerified() {
        return (root, query, cb) -> cb.isTrue(root.get("verified"));
    }

    public static Specification<Professional> hasCity(String city) {
        return (root, query, cb) -> {
            if (city == null) return null;
            // NOTE: case-insensitive match; add a functional index on lower(city) in production for performance
            return cb.equal(cb.lower(root.get("city")), city.strip().toLowerCase());
        };
    }

    public static Specification<Professional> hasSpecialization(String spec) {
        return (root, query, cb) -> {
            if (spec == null) return null;
            String escaped = spec.strip().toLowerCase()
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
            return cb.like(cb.lower(root.get("specialization")), "%" + escaped + "%", '\\');
        };
    }

    public static Specification<Professional> hasMinRate(BigDecimal min) {
        return (root, query, cb) ->
            min == null ? null : cb.greaterThanOrEqualTo(root.get("hourlyRate"), min);
    }

    public static Specification<Professional> hasMaxRate(BigDecimal max) {
        return (root, query, cb) ->
            max == null ? null : cb.lessThanOrEqualTo(root.get("hourlyRate"), max);
    }
}
