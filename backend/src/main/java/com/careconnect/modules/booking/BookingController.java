package com.careconnect.modules.booking;

import com.careconnect.modules.auth.entity.User;
import com.careconnect.modules.booking.dto.BookingResponse;
import com.careconnect.modules.booking.dto.CreateBookingRequest;
import com.careconnect.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
            bookingService.create(user.getEmail(), request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>> list(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(
            bookingService.getMyBookings(user.getEmail())));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancel(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
            bookingService.cancel(id, user.getEmail())));
    }
}
