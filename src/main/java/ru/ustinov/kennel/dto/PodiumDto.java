package ru.ustinov.kennel.dto;

// строка протокола нативного запроса (призёры выставки)
public record PodiumDto(
        int place,
        String dogName,
        String breed,
        int score
) {
}
