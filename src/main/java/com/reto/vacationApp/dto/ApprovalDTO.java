package com.reto.vacationApp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApprovalDTO {
    @NotBlank
    private String comment;
}