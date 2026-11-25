package ru.ustinov.kennel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ExhibitionCreateDto(
        @NotBlank String title,
        @NotBlank String city,
        @NotNull LocalDate eventDate
) {
}
