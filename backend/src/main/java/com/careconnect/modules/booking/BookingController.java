package com.careconnect.modules.booking;

import com.careconnect.shared.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<Object>>> list() {
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }
}
