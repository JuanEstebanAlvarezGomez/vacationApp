package com.reto.vacationApp.controller;

import com.reto.vacationApp.dto.LeaveRequestDTO;
import com.reto.vacationApp.entity.LeaveRequest;
import com.reto.vacationApp.service.LeaveRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final LeaveRequestService requestService;

    @PostMapping("/requests")
    public ResponseEntity<LeaveRequest> createRequest(
            @Valid @RequestBody LeaveRequestDTO dto,
            Authentication auth) {
        String username = auth.getName();
        LeaveRequest created = requestService.createRequest(dto, username);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<LeaveRequest>> getMyRequests(Authentication auth) {
        String username = auth.getName();
        List<LeaveRequest> requests = requestService.getRequestsByEmployee(username);
        return ResponseEntity.ok(requests);
    }
}