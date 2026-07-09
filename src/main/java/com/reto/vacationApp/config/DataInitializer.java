package com.reto.vacationApp.config;

import com.reto.vacationApp.entity.User;
import com.reto.vacationApp.entity.LeaveBalance;
import com.reto.vacationApp.enums.Role;
import com.reto.vacationApp.repository.UserRepository;
import com.reto.vacationApp.repository.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LeaveBalanceRepository balanceRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User emp = new User(null, "employee1", "Empleado Uno", Role.EMPLOYEE);
            User boss = new User(null, "boss1", "Jefe Uno", Role.BOSS);
            User hr = new User(null, "hr1", "RRHH Uno", Role.HR);
            userRepository.save(emp);
            userRepository.save(boss);
            userRepository.save(hr);

            LeaveBalance balance = new LeaveBalance();
            balance.setUser(emp);
            balance.setYear(Year.now().getValue());
            balance.setTotalDays(15);
            balance.setUsedDays(0);
            balance.setRemainingDays(15);
            balanceRepository.save(balance);

            log.info("Datos de prueba inicializados.");
        }
    }
}