package com.reto.vacationApp.service;

import com.reto.vacationApp.entity.LeaveBalance;
import com.reto.vacationApp.entity.User;
import com.reto.vacationApp.enums.Role;
import com.reto.vacationApp.exception.BusinessException;
import com.reto.vacationApp.repository.LeaveBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private LeaveBalanceRepository balanceRepository;

    @InjectMocks
    private BalanceService balanceService;

    private User user;
    private LeaveBalance balance;

    @BeforeEach
    void setUp() {
        user = new User(1L, "employee1", "Empleado", Role.EMPLOYEE);
        balance = new LeaveBalance();
        balance.setUser(user);
        balance.setYear(Year.now().getValue());
        balance.setTotalDays(15);
        balance.setUsedDays(3);
        balance.setRemainingDays(12);
    }

    @Test
    void getRemainingDays_existingBalance() {
        when(balanceRepository.findByUserAndYear(user, Year.now().getValue()))
                .thenReturn(Optional.of(balance));

        int remaining = balanceService.getRemainingDays(user, Year.now().getValue());
        assertEquals(12, remaining);
    }

    @SuppressWarnings("null")
    @Test
    void getRemainingDays_newBalance_createsDefault() {
        when(balanceRepository.findByUserAndYear(user, Year.now().getValue()))
                .thenReturn(Optional.empty());
        when(balanceRepository.save(any(LeaveBalance.class))).thenAnswer(inv -> inv.getArgument(0));

        int remaining = balanceService.getRemainingDays(user, Year.now().getValue());
        assertEquals(15, remaining);
        verify(balanceRepository, times(1)).save(any(LeaveBalance.class));
    }

    @SuppressWarnings("null")
    @Test
    void consumeDays_success() {
        when(balanceRepository.findByUserAndYear(user, Year.now().getValue()))
                .thenReturn(Optional.of(balance));
        when(balanceRepository.save(any(LeaveBalance.class))).thenReturn(balance);

        balanceService.consumeDays(user, Year.now().getValue(), 3);

        assertEquals(6, balance.getUsedDays());
        assertEquals(9, balance.getRemainingDays());
    }

    @Test
    void consumeDays_insufficientBalance_throwsException() {
        when(balanceRepository.findByUserAndYear(user, Year.now().getValue()))
                .thenReturn(Optional.of(balance));

        assertThrows(BusinessException.class, () ->
                balanceService.consumeDays(user, Year.now().getValue(), 13));
    }
}