package com.careconnect.modules.reviews.entity;

import com.careconnect.modules.auth.entity.User;
import com.careconnect.modules.booking.entity.Booking;
import com.careconnect.modules.professionals.entity.Professional;
import com.careconnect.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;

    @Column(nullable = false)
    private short rating;

    private String comment;
}
