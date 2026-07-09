package com.reto.vacationApp.service;

import com.reto.vacationApp.entity.LeaveRequest;
import com.reto.vacationApp.entity.User;
import com.reto.vacationApp.enums.Role;
import com.reto.vacationApp.enums.RequestStatus;
import com.reto.vacationApp.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApprovalServiceTest {

    private final ApprovalService approvalService = new ApprovalService();

    @Test
    void validateBossApproval_valid() {
        User employee = new User(1L, "emp", "E", Role.EMPLOYEE);
        User boss = new User(2L, "boss", "B", Role.BOSS);
        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setStatus(RequestStatus.PENDING_BOSS);

        assertDoesNotThrow(() -> approvalService.validateBossApproval(request, boss));
    }

    @Test
    void validateBossApproval_sameUser_throws() {
        User employee = new User(1L, "emp", "E", Role.EMPLOYEE);
        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setStatus(RequestStatus.PENDING_BOSS);

        assertThrows(BusinessException.class, () ->
                approvalService.validateBossApproval(request, employee));
    }

    @Test
    void validateHRConfirmation_valid() {
        User employee = new User(1L, "emp", "E", Role.EMPLOYEE);
        User hr = new User(3L, "hr", "HR", Role.HR);
        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setStatus(RequestStatus.PENDING_HR);

        assertDoesNotThrow(() -> approvalService.validateHRConfirmation(request, hr));
    }

    @Test
    void validateHRConfirmation_wrongStatus_throws() {
        User employee = new User(1L, "emp", "E", Role.EMPLOYEE);
        User hr = new User(3L, "hr", "HR", Role.HR);
        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setStatus(RequestStatus.APPROVED_BY_BOSS);

        assertThrows(BusinessException.class, () ->
                approvalService.validateHRConfirmation(request, hr));
    }
}