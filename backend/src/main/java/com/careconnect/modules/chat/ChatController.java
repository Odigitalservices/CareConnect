package com.careconnect.modules.chat;

import com.careconnect.shared.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ChatController {

    @GetMapping
    public ResponseEntity<ApiResponse<List<Object>>> list() {
        return ResponseEntity.ok(ApiResponse.success(List.of()));
    }
}
