package com.reto.vacationApp.controller;

import com.reto.vacationApp.dto.ApprovalDTO;
import com.reto.vacationApp.entity.LeaveBalance;
import com.reto.vacationApp.entity.LeaveRequest;
import com.reto.vacationApp.entity.User;
import com.reto.vacationApp.repository.UserRepository;
import com.reto.vacationApp.service.BalanceService;
import com.reto.vacationApp.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class HRController {

    private final LeaveRequestService requestService;
    private final BalanceService balanceService;
    private final UserRepository userRepository;

    @GetMapping("/pending")
    public ResponseEntity<List<LeaveRequest>> getPendingForHR() {
        return ResponseEntity.ok(requestService.getPendingForHR());
    }

    @PutMapping("/requests/{id}/confirm")
    public ResponseEntity<LeaveRequest> confirmByHR(
            @PathVariable Long id,
            @Valid @RequestBody ApprovalDTO dto,
            Authentication auth) {
        LeaveRequest updated = requestService.confirmByHR(id, dto, auth.getName());
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/requests/{id}/reject")
    public ResponseEntity<LeaveRequest> rejectByHR(
            @PathVariable Long id,
            @Valid @RequestBody ApprovalDTO dto,
            Authentication auth) {
        LeaveRequest updated = requestService.rejectByHR(id, dto, auth.getName());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/balances/{employeeId}")
    public ResponseEntity<LeaveBalance> getBalance(@PathVariable Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        LeaveBalance balance = balanceService.getOrCreateBalance(employee, Year.now().getValue());
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/balances/my-balance")
    public ResponseEntity<LeaveBalance> getMyBalance(Authentication auth) {
        User employee = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        LeaveBalance balance = balanceService.getOrCreateBalance(employee, Year.now().getValue());
        return ResponseEntity.ok(balance);
    }
}