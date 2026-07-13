package com.reto.vacationApp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApprovalDTO {

    @NotBlank(message = "El comentario es obligatorio.")
    private String comment;
}