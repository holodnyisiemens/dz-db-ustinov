package ru.ustinov.kennel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.ustinov.kennel.entity.Gender;

import java.time.LocalDate;

public record DogCreateDto(
        @NotBlank String name,
        @NotBlank String breed,
        @NotNull Gender gender,
        @NotNull LocalDate birthDate,
        Long litterId
) {
}
