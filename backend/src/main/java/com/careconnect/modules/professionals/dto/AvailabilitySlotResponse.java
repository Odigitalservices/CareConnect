package com.careconnect.modules.professionals.dto;
import java.time.Instant;
import java.util.UUID;

public record AvailabilitySlotResponse(UUID id, Instant startTime, Instant endTime) {}
