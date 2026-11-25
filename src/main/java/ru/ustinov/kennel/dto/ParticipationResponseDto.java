package ru.ustinov.kennel.dto;

import ru.ustinov.kennel.entity.Participation;

public record ParticipationResponseDto(
        Long id,
        Long dogId,
        String dogName,
        Long exhibitionId,
        String exhibitionTitle,
        int score,
        int place
) {

    public static ParticipationResponseDto from(Participation participation) {
        return new ParticipationResponseDto(
                participation.getId(),
                participation.getDog().getId(),
                participation.getDog().getName(),
                participation.getExhibition().getId(),
                participation.getExhibition().getTitle(),
                participation.getScore(),
                participation.getPlace()
        );
    }
}
