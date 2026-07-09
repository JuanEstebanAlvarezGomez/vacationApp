package com.reto.vacationApp.service;

import com.reto.vacationApp.dto.LeaveRequestDTO;
import com.reto.vacationApp.dto.ApprovalDTO;
import com.reto.vacationApp.entity.LeaveRequest;
import com.reto.vacationApp.entity.User;
import com.reto.vacationApp.enums.RequestStatus;
import com.reto.vacationApp.exception.BusinessException;
import com.reto.vacationApp.repository.LeaveRequestRepository;
import com.reto.vacationApp.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class LeaveRequestService {

    private final LeaveRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final BalanceService balanceService;
    private final ApprovalService approvalService;
    private final HolidayService holidayService;

    public LeaveRequestService(LeaveRequestRepository requestRepository,
                               UserRepository userRepository,
                               BalanceService balanceService,
                               ApprovalService approvalService,
                               HolidayService holidayService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.balanceService = balanceService;
        this.approvalService = approvalService;
        this.holidayService = holidayService;
    }

    @Transactional
    public LeaveRequest createRequest(LeaveRequestDTO dto, String username) {
        User employee = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new BusinessException("La fecha de inicio debe ser anterior a la fecha de fin.");
        }
        if (dto.getStartDate().isBefore(LocalDate.now())) {
            throw new BusinessException("No se pueden solicitar vacaciones en fechas pasadas.");
        }

        long workingDays = holidayService.countWorkingDays(dto.getStartDate(), dto.getEndDate());
        if (workingDays == 0) {
            throw new BusinessException("El período seleccionado no tiene días hábiles.");
        }

        if (!balanceService.hasSufficientDays(employee, (int) workingDays)) {
            throw new BusinessException("Saldo insuficiente. Días disponibles: " +
                    balanceService.getRemainingDays(employee, LocalDate.now().getYear()));
        }

        List<LeaveRequest> existing = requestRepository.findByEmployeeAndStatusIn(employee,
                List.of(RequestStatus.PENDING_BOSS, RequestStatus.APPROVED_BY_BOSS,
                        RequestStatus.PENDING_HR, RequestStatus.CONFIRMED_BY_HR));
        for (LeaveRequest existingReq : existing) {
            if (datesOverlap(dto.getStartDate(), dto.getEndDate(),
                    existingReq.getStartDate(), existingReq.getEndDate())) {
                throw new BusinessException("Ya tienes una solicitud en ese período (estado: " +
                        existingReq.getStatus() + ")");
            }
        }

        LeaveRequest request = new LeaveRequest();
        request.setEmployee(employee);
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setReason(dto.getReason());
        request.setStatus(RequestStatus.PENDING_BOSS);
        request.setCreatedAt(LocalDateTime.now());

        return requestRepository.save(request);
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !(start1.isAfter(end2) || end1.isBefore(start2));
    }

    public List<LeaveRequest> getRequestsByEmployee(String username) {
        User employee = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        return requestRepository.findByEmployee(employee);
    }

    public LeaveRequest getRequest(@NonNull Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Solicitud no encontrada"));
    }

    @Transactional
    public LeaveRequest approveByBoss(@NonNull Long requestId, ApprovalDTO dto, String username) {
        LeaveRequest request = getRequest(requestId);
        User boss = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Jefe no encontrado"));

        approvalService.validateBossApproval(request, boss);

        request.setStatus(RequestStatus.APPROVED_BY_BOSS);
        request.setBossComment(dto.getComment());
        request.setUpdatedAt(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING_HR);
        return requestRepository.save(request);
    }

    @Transactional
    public LeaveRequest rejectByBoss(@NonNull Long requestId, ApprovalDTO dto, String username) {
        LeaveRequest request = getRequest(requestId);
        User boss = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Jefe no encontrado"));

        approvalService.validateBossApproval(request, boss);

        request.setStatus(RequestStatus.REJECTED_BY_BOSS);
        request.setBossComment(dto.getComment());
        request.setUpdatedAt(LocalDateTime.now());
        return requestRepository.save(request);
    }

    @Transactional
    public LeaveRequest confirmByHR(@NonNull Long requestId, ApprovalDTO dto, String username) {
        LeaveRequest request = getRequest(requestId);
        User hr = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario de RRHH no encontrado"));

        approvalService.validateHRConfirmation(request, hr);

        long workingDays = holidayService.countWorkingDays(request.getStartDate(), request.getEndDate());
        balanceService.consumeDays(request.getEmployee(), LocalDate.now().getYear(), (int) workingDays);

        request.setStatus(RequestStatus.CONFIRMED_BY_HR);
        request.setHrComment(dto.getComment());
        request.setUpdatedAt(LocalDateTime.now());
        return requestRepository.save(request);
    }

    @Transactional
    public LeaveRequest rejectByHR(@NonNull Long requestId, ApprovalDTO dto, String username) {
        LeaveRequest request = getRequest(requestId);
        User hr = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario de RRHH no encontrado"));

        approvalService.validateHRConfirmation(request, hr);

        request.setStatus(RequestStatus.REJECTED_BY_HR);
        request.setHrComment(dto.getComment());
        request.setUpdatedAt(LocalDateTime.now());
        return requestRepository.save(request);
    }

    public List<LeaveRequest> getPendingForBoss() {
        return requestRepository.findByStatus(RequestStatus.PENDING_BOSS);
    }

    public List<LeaveRequest> getPendingForHR() {
        return requestRepository.findByStatus(RequestStatus.PENDING_HR);
    }
}