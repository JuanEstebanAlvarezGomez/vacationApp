package com.reto.vacationApp.service;

import com.reto.vacationApp.dto.ApprovalDTO;
import com.reto.vacationApp.dto.LeaveRequestDTO;
import com.reto.vacationApp.entity.LeaveRequest;
import com.reto.vacationApp.entity.User;
import com.reto.vacationApp.enums.Role;
import com.reto.vacationApp.enums.RequestStatus;
import com.reto.vacationApp.exception.BusinessException;
import com.reto.vacationApp.repository.LeaveRequestRepository;
import com.reto.vacationApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class LeaveRequestServiceTest {

    @Mock
    private LeaveRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BalanceService balanceService;

    @Spy
    private ApprovalService approvalService;

    @Mock
    private HolidayService holidayService;

    @InjectMocks
    private LeaveRequestService service;

    private User employee;
    private User boss;
    private LeaveRequestDTO dto;

    @BeforeEach
    void setUp() {
        employee = new User(1L, "employee1", "Empleado", Role.EMPLOYEE);
        boss = new User(2L, "boss1", "Jefe", Role.BOSS);
        dto = new LeaveRequestDTO();
        dto.setStartDate(LocalDate.now().plusDays(5));
        dto.setEndDate(LocalDate.now().plusDays(7));
        dto.setReason("Vacaciones personales");
    }

    @Test
    void createRequest_success() {
        when(userRepository.findByUsername("employee1")).thenReturn(Optional.of(employee));
        when(holidayService.countWorkingDays(any(LocalDate.class), any(LocalDate.class))).thenReturn(3L);
        when(balanceService.hasSufficientDays(any(User.class), anyInt())).thenReturn(true);
        when(requestRepository.findByEmployeeAndStatusIn(any(User.class), anyList())).thenReturn(java.util.Collections.emptyList());
        when(requestRepository.save(any(LeaveRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        LeaveRequest result = service.createRequest(dto, "employee1");

        assertNotNull(result);
        assertEquals(RequestStatus.PENDING_BOSS, result.getStatus());
        verify(requestRepository, times(1)).save(any(LeaveRequest.class));
    }

    @Test
    void createRequest_insufficientBalance_throwsException() {
        when(userRepository.findByUsername("employee1")).thenReturn(Optional.of(employee));
        when(holidayService.countWorkingDays(any(LocalDate.class), any(LocalDate.class))).thenReturn(5L);
        when(balanceService.hasSufficientDays(any(User.class), anyInt())).thenReturn(false);

        assertThrows(BusinessException.class, () -> service.createRequest(dto, "employee1"));
        verify(requestRepository, never()).save(any(LeaveRequest.class));
    }

    @Test
    void approveByBoss_success() {
        LeaveRequest request = new LeaveRequest();
        request.setId(1L);
        request.setEmployee(employee);
        request.setStatus(RequestStatus.PENDING_BOSS);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(userRepository.findByUsername("boss1")).thenReturn(Optional.of(boss));
        when(requestRepository.save(any(LeaveRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        ApprovalDTO approvalDto = new ApprovalDTO();
        approvalDto.setComment("Aprobado");

        LeaveRequest result = service.approveByBoss(1L, approvalDto, "boss1");

        assertEquals(RequestStatus.PENDING_HR, result.getStatus());
        assertEquals("Aprobado", result.getBossComment());
    }

    @Test
    void approveByBoss_wrongStatus_throwsException() {
        LeaveRequest request = new LeaveRequest();
        request.setId(1L);
        request.setEmployee(employee);
        request.setStatus(RequestStatus.REJECTED_BY_BOSS);

        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(userRepository.findByUsername("boss1")).thenReturn(Optional.of(boss));

        ApprovalDTO approvalDto = new ApprovalDTO();
        approvalDto.setComment("Aprobado");

        assertThrows(BusinessException.class, () -> service.approveByBoss(1L, approvalDto, "boss1"));
    }
}