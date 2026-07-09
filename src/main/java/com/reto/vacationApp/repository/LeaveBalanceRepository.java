package com.reto.vacationApp.repository;

import com.reto.vacationApp.entity.LeaveBalance;
import com.reto.vacationApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    Optional<LeaveBalance> findByUserAndYear(User user, int year);
}