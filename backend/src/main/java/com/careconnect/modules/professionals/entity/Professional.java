package com.careconnect.modules.professionals.entity;

import com.careconnect.modules.auth.entity.User;
import com.careconnect.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "professionals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Professional extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String specialization;

    private String bio;

    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @Column(nullable = false)
    private boolean verified = false;

    private String city;

    @Column(name = "hourly_rate")
    private BigDecimal hourlyRate;
}
