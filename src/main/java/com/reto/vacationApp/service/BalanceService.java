package com.reto.vacationApp.service;

import com.reto.vacationApp.entity.LeaveBalance;
import com.reto.vacationApp.entity.User;
import com.reto.vacationApp.exception.BusinessException;
import com.reto.vacationApp.repository.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final LeaveBalanceRepository balanceRepository;

    public LeaveBalance getOrCreateBalance(User user, int year) {
        return balanceRepository.findByUserAndYear(user, year)
                .orElseGet(() -> {
                    LeaveBalance newBalance = new LeaveBalance();
                    newBalance.setUser(user);
                    newBalance.setYear(year);
                    newBalance.setTotalDays(15);
                    newBalance.setUsedDays(0);
                    newBalance.setRemainingDays(15);
                    return balanceRepository.save(newBalance);
                });
    }

    public int getRemainingDays(User user, int year) {
        LeaveBalance balance = getOrCreateBalance(user, year);
        return balance.getRemainingDays();
    }

    public boolean hasSufficientDays(User user, int requestedDays) {
        return getRemainingDays(user, Year.now().getValue()) >= requestedDays;
    }

    @Transactional
    public void consumeDays(User user, int year, int days) {
        LeaveBalance balance = getOrCreateBalance(user, year);
        if (balance.getRemainingDays() < days) {
            throw new BusinessException("Saldo insuficiente para consumir " + days + " días.");
        }
        balance.setUsedDays(balance.getUsedDays() + days);
        balance.setRemainingDays(balance.getRemainingDays() - days);
        balanceRepository.save(balance);
    }
}