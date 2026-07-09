package com.reto.vacationApp.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
public class HolidayService {

    private static final Set<LocalDate> HOLIDAYS_2026 = new HashSet<>();

    static {
        HOLIDAYS_2026.add(LocalDate.of(2026, 1, 1));
        HOLIDAYS_2026.add(LocalDate.of(2026, 1, 12));
        HOLIDAYS_2026.add(LocalDate.of(2026, 3, 23));
        HOLIDAYS_2026.add(LocalDate.of(2026, 4, 6));
        HOLIDAYS_2026.add(LocalDate.of(2026, 4, 7));
        HOLIDAYS_2026.add(LocalDate.of(2026, 5, 1));
        HOLIDAYS_2026.add(LocalDate.of(2026, 5, 11));
        HOLIDAYS_2026.add(LocalDate.of(2026, 6, 1));
        HOLIDAYS_2026.add(LocalDate.of(2026, 6, 15));
        HOLIDAYS_2026.add(LocalDate.of(2026, 6, 29));
        HOLIDAYS_2026.add(LocalDate.of(2026, 7, 20));
        HOLIDAYS_2026.add(LocalDate.of(2026, 8, 7));
        HOLIDAYS_2026.add(LocalDate.of(2026, 8, 17));
        HOLIDAYS_2026.add(LocalDate.of(2026, 10, 12));
        HOLIDAYS_2026.add(LocalDate.of(2026, 11, 2));
        HOLIDAYS_2026.add(LocalDate.of(2026, 11, 16));
        HOLIDAYS_2026.add(LocalDate.of(2026, 12, 8));
        HOLIDAYS_2026.add(LocalDate.of(2026, 12, 25));
    }

    public long countWorkingDays(LocalDate start, LocalDate end) {
        long workingDays = 0;
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (isWorkingDay(date)) {
                workingDays++;
            }
        }
        return workingDays;
    }

    public boolean isWorkingDay(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        boolean weekend = dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
        boolean holiday = HOLIDAYS_2026.contains(date);
        return !weekend && !holiday;
    }
}