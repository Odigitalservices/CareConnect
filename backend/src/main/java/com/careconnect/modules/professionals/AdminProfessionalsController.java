package com.careconnect.modules.professionals;

import com.careconnect.modules.professionals.dto.*;
import com.careconnect.shared.dto.ApiResponse;
import com.careconnect.shared.dto.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/professionals")
@RequiredArgsConstructor
public class AdminProfessionalsController {

    private final AdminProfessionalsService adminProfessionalsService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ProfessionalAdminResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            adminProfessionalsService.listAll(page, size)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProfessionalAdminResponse>> create(
            @Valid @RequestBody CreateProfessionalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
            adminProfessionalsService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfessionalAdminResponse>> update(
            @PathVariable UUID id,
            @RequestBody UpdateProfessionalRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
            adminProfessionalsService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        adminProfessionalsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/verify")
    public ResponseEntity<ApiResponse<ProfessionalAdminResponse>> toggleVerify(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
            adminProfessionalsService.toggleVerify(id)));
    }
}
