package ru.ustinov.kennel.dto;

import ru.ustinov.kennel.entity.Dog;
import ru.ustinov.kennel.entity.Gender;
import ru.ustinov.kennel.entity.Litter;
import ru.ustinov.kennel.entity.VeterinaryPassport;

import java.time.LocalDate;

public record DogResponseDto(
        Long id,
        String name,
        String breed,
        Gender gender,
        LocalDate birthDate,
        long version,
        Long litterId,
        String litterName,
        Long passportId,
        String passportNumber
) {

    public static DogResponseDto from(Dog dog) {
        Litter litter = dog.getLitter();
        VeterinaryPassport passport = dog.getPassport();
        return new DogResponseDto(
                dog.getId(),
                dog.getName(),
                dog.getBreed(),
                dog.getGender(),
                dog.getBirthDate(),
                dog.getVersion(),
                litter != null ? litter.getId() : null,
                litter != null ? litter.getName() : null,
                passport != null ? passport.getId() : null,
                passport != null ? passport.getPassportNumber() : null
        );
    }
}
