package ru.ustinov.kennel.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.kennel.dto.DogCreateDto;
import ru.ustinov.kennel.dto.DogResponseDto;
import ru.ustinov.kennel.dto.LitterCreateDto;
import ru.ustinov.kennel.dto.LitterResponseDto;
import ru.ustinov.kennel.entity.Dog;
import ru.ustinov.kennel.entity.Litter;
import ru.ustinov.kennel.repository.DogRepository;
import ru.ustinov.kennel.repository.LitterRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class LitterService {

    private final LitterRepository litterRepository;
    private final DogRepository dogRepository;

    public LitterService(LitterRepository litterRepository, DogRepository dogRepository) {
        this.litterRepository = litterRepository;
        this.dogRepository = dogRepository;
    }

    @Transactional
    public LitterResponseDto create(LitterCreateDto dto) {
        Litter litter = new Litter(dto.name(), dto.birthDate());
        return LitterResponseDto.from(litterRepository.save(litter));
    }

    @Transactional(readOnly = true)
    public LitterResponseDto getById(Long id) {
        // findDetailById — @EntityGraph подгружает LAZY-коллекцию dogs одним запросом
        return LitterResponseDto.from(findLitter(id));
    }

    @Transactional(readOnly = true)
    public List<LitterResponseDto> findBigLitters(LocalDate from, int minDogs) {
        // нативный SQL-запрос: приплоды не моложе даты, где собак не меньше minDogs
        return litterRepository.findWithAtLeastDogsBornAfter(from, minDogs).stream()
                .map(LitterResponseDto::summary)
                .toList();
    }

    @Transactional
    public DogResponseDto addDog(Long litterId, DogCreateDto dto) {
        Litter litter = findLitter(litterId);
        Dog dog = new Dog(dto.name(), dto.breed(), dto.gender(), dto.birthDate());
        litter.addDog(dog); // синхронизация обеих сторон двусторонней связи
        // собака — владеющая сторона связи (держит litter_id), поэтому сохраняем её
        return DogResponseDto.from(dogRepository.save(dog));
    }

    @Transactional
    public void removeDog(Long litterId, Long dogId) {
        Litter litter = findLitter(litterId);
        Dog dog = litter.getDogs().stream()
                .filter(d -> dogId.equals(d.getId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Собака с id=" + dogId + " не входит в приплод с id=" + litterId));
        // orphanRemoval = true: удалённая из коллекции собака будет удалена и из БД
        litter.removeDog(dog);
    }

    private Litter findLitter(Long id) {
        return litterRepository.findDetailById(id)
                .orElseThrow(() -> new EntityNotFoundException("Приплод с id=" + id + " не найден"));
    }
}
