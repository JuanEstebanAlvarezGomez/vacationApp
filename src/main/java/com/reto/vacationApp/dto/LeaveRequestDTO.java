package com.reto.vacationApp.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequestDTO {

    @NotNull(message = "La fecha de inicio es obligatoria.")
    @Future(message = "La fecha de inicio debe ser futura.")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es obligatoria.")
    @Future(message = "La fecha de fin debe ser futura.")
    private LocalDate endDate;

    @NotBlank(message = "El motivo es obligatorio.")
    private String reason;
}