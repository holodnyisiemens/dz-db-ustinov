package ru.ustinov.kennel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ParticipationCreateDto(
        @NotNull Long dogId,
        @NotNull Long exhibitionId,
        @Min(1) @Max(100) int score,
        @Min(1) int place
) {
}
