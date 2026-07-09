package com.reto.vacationApp.repository;

import com.reto.vacationApp.entity.LeaveRequest;
import com.reto.vacationApp.entity.User;
import com.reto.vacationApp.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployee(User employee);
    List<LeaveRequest> findByEmployeeAndStatusIn(User employee, List<RequestStatus> statuses);
    List<LeaveRequest> findByStatus(RequestStatus status);
}