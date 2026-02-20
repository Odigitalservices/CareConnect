package com.careconnect.modules.reviews.repository;

import com.careconnect.modules.reviews.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByProfessionalId(UUID professionalId);
    Optional<Review> findByBookingId(UUID bookingId);
}
