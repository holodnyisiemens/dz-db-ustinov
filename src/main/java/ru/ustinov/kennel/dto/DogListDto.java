package ru.ustinov.kennel.dto;

import ru.ustinov.kennel.entity.Dog;
import ru.ustinov.kennel.entity.Gender;

import java.time.LocalDate;

public record DogListDto(
        Long id,
        String name,
        String breed,
        Gender gender,
        LocalDate birthDate
) {

    public static DogListDto from(Dog dog) {
        return new DogListDto(dog.getId(), dog.getName(), dog.getBreed(),
                dog.getGender(), dog.getBirthDate());
    }
}
