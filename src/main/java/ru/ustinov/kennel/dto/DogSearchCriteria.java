package ru.ustinov.kennel.dto;

import ru.ustinov.kennel.entity.Gender;

import java.time.LocalDate;

// необязательные фильтры для динамического поиска через Criteria API
public record DogSearchCriteria(
        String breed,
        Gender gender,
        LocalDate bornAfter,
        LocalDate bornBefore,
        String nameContains
) {
}
