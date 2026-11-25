package ru.ustinov.kennel.dto;

import ru.ustinov.kennel.entity.Exhibition;

import java.time.LocalDate;
import java.util.List;

public record ExhibitionResponseDto(
        Long id,
        String title,
        String city,
        LocalDate eventDate,
        List<ParticipationResponseDto> participations
) {

    public static ExhibitionResponseDto from(Exhibition exhibition) {
        return new ExhibitionResponseDto(
                exhibition.getId(),
                exhibition.getTitle(),
                exhibition.getCity(),
                exhibition.getEventDate(),
                exhibition.getParticipations().stream().map(ParticipationResponseDto::from).toList()
        );
    }

    public static ExhibitionResponseDto summary(Exhibition exhibition) {
        return new ExhibitionResponseDto(exhibition.getId(), exhibition.getTitle(),
                exhibition.getCity(), exhibition.getEventDate(), List.of());
    }
}
