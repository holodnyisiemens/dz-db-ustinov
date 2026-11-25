package ru.ustinov.kennel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PassportCreateDto(
        @NotBlank String passportNumber,
        @NotNull LocalDate issueDate,
        @NotBlank String veterinarianName,
        @NotNull Long dogId
) {
}
