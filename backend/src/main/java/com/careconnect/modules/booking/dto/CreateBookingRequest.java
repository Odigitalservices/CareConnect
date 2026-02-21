package com.careconnect.modules.booking.dto;

import com.careconnect.modules.booking.entity.VisitType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateBookingRequest(
    @NotNull UUID slotId,
    @NotNull VisitType visitType,
    String address,
    String notes
) {
    @AssertTrue(message = "Address is required for HOME visits")
    public boolean isAddressValidForVisitType() {
        if (visitType == null) return true; // @NotNull handles null visitType
        return visitType != VisitType.HOME || (address != null && !address.isBlank());
    }
}
