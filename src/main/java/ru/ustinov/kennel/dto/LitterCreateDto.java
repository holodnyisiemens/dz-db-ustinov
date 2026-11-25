package ru.ustinov.kennel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LitterCreateDto(
        @NotBlank String name,
        @NotNull LocalDate birthDate
) {
}
