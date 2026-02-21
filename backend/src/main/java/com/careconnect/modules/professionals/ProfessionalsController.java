package com.careconnect.modules.professionals;

import com.careconnect.modules.professionals.dto.ProfessionalDetailResponse;
import com.careconnect.modules.professionals.dto.ProfessionalSummaryResponse;
import com.careconnect.shared.dto.ApiResponse;
import com.careconnect.shared.dto.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/professionals")
@RequiredArgsConstructor
public class ProfessionalsController {

    private final ProfessionalsService professionalsService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProfessionalSummaryResponse>>> list(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) BigDecimal minRate,
            @RequestParam(required = false) BigDecimal maxRate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            professionalsService.listProfessionals(city, specialization, minRate, maxRate, page, size)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfessionalDetailResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
            professionalsService.getProfessionalById(id)
        ));
    }
}
