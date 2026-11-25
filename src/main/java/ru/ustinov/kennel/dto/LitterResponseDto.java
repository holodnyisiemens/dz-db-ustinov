package ru.ustinov.kennel.dto;

import ru.ustinov.kennel.entity.Litter;

import java.time.LocalDate;
import java.util.List;

public record LitterResponseDto(
        Long id,
        String name,
        LocalDate birthDate,
        List<DogListDto> dogs
) {

    public static LitterResponseDto from(Litter litter) {
        return new LitterResponseDto(
                litter.getId(),
                litter.getName(),
                litter.getBirthDate(),
                litter.getDogs().stream().map(DogListDto::from).toList()
        );
    }

    // для ответов, где состав собак не подгружался (например, результат нативного запроса)
    public static LitterResponseDto summary(Litter litter) {
        return new LitterResponseDto(litter.getId(), litter.getName(), litter.getBirthDate(), List.of());
    }
}
