package ru.ustinov.kennel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.ustinov.kennel.entity.Gender;

public record DogUpdateDto(
        @NotBlank String name,
        @NotBlank String breed,
        @NotNull Gender gender,
        @NotNull Long version
) {
}
