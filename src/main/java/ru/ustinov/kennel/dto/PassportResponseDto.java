package ru.ustinov.kennel.dto;

import ru.ustinov.kennel.entity.VeterinaryPassport;

import java.time.LocalDate;

public record PassportResponseDto(
        Long id,
        String passportNumber,
        LocalDate issueDate,
        String veterinarianName,
        Long dogId,
        String dogName
) {

    public static PassportResponseDto from(VeterinaryPassport passport) {
        return new PassportResponseDto(
                passport.getId(),
                passport.getPassportNumber(),
                passport.getIssueDate(),
                passport.getVeterinarianName(),
                passport.getDog().getId(),
                passport.getDog().getName()
        );
    }
}
