package com.reto.vacationApp.controller;

import com.reto.vacationApp.dto.ApprovalDTO;
import com.reto.vacationApp.entity.LeaveRequest;
import com.reto.vacationApp.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boss")
@RequiredArgsConstructor
public class BossController {

    private final LeaveRequestService requestService;

    @GetMapping("/pending")
    public ResponseEntity<List<LeaveRequest>> getPendingForBoss() {
        return ResponseEntity.ok(requestService.getPendingForBoss());
    }

    @PutMapping("/requests/{id}/approve")
    public ResponseEntity<LeaveRequest> approveByBoss(
            @PathVariable @NonNull Long id,
            @Valid @RequestBody ApprovalDTO dto,
            Authentication auth) {
        LeaveRequest updated = requestService.approveByBoss(id, dto, auth.getName());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/requests/{id}/reject")
    public ResponseEntity<LeaveRequest> rejectByBoss(
            @PathVariable @NonNull Long id,
            @Valid @RequestBody ApprovalDTO dto,
            Authentication auth) {
        LeaveRequest updated = requestService.rejectByBoss(id, dto, auth.getName());
        return ResponseEntity.ok(updated);
    }
}