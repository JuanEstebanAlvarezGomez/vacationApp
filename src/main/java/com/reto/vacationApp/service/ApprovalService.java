package com.reto.vacationApp.service;

import com.reto.vacationApp.entity.LeaveRequest;
import com.reto.vacationApp.entity.User;
import com.reto.vacationApp.enums.RequestStatus;
import com.reto.vacationApp.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class ApprovalService {

    public void validateBossApproval(LeaveRequest request, User boss) {
        if (!RequestStatus.PENDING_BOSS.equals(request.getStatus())) {
            throw new BusinessException("La solicitud no está en estado pendiente de jefe.");
        }
        if (request.getEmployee().equals(boss)) {
            throw new BusinessException("Un jefe no puede aprobar su propia solicitud.");
        }
    }

    public void validateHRConfirmation(LeaveRequest request, User hr) {
        if (!RequestStatus.PENDING_HR.equals(request.getStatus())) {
            throw new BusinessException("La solicitud no está en estado pendiente de RRHH.");
        }
        if (request.getEmployee().equals(hr)) {
            throw new BusinessException("RRHH no puede confirmar su propia solicitud.");
        }
    }
}