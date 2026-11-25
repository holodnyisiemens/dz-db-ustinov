package ru.ustinov.kennel.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.ustinov.kennel.dto.DogCreateDto;
import ru.ustinov.kennel.dto.DogListDto;
import ru.ustinov.kennel.dto.DogResponseDto;
import ru.ustinov.kennel.dto.DogSearchCriteria;
import ru.ustinov.kennel.dto.DogUpdateDto;
import ru.ustinov.kennel.entity.Dog;
import ru.ustinov.kennel.entity.Litter;
import ru.ustinov.kennel.exception.ConflictException;
import ru.ustinov.kennel.repository.DogRepository;
import ru.ustinov.kennel.repository.LitterRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DogService {

    private final DogRepository dogRepository;
    private final LitterRepository litterRepository;
    private final EntityManager entityManager;

    public DogService(DogRepository dogRepository, LitterRepository litterRepository, EntityManager entityManager) {
        this.dogRepository = dogRepository;
        this.litterRepository = litterRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public DogResponseDto create(DogCreateDto dto) {
        Dog dog = new Dog(dto.name(), dto.breed(), dto.gender(), dto.birthDate());
        if (dto.litterId() != null) {
            Litter litter = litterRepository.findById(dto.litterId())
                    .orElseThrow(() -> new EntityNotFoundException("Приплод с id=" + dto.litterId() + " не найден"));
            // вспомогательный метод синхронизирует обе стороны двусторонней связи
            litter.addDog(dog);
        }
        return DogResponseDto.from(dogRepository.save(dog));
    }

    @Transactional(readOnly = true)
    public DogResponseDto getById(Long id) {
        // findDetailById — JPQL с join fetch ленивых litter/passport
        Dog dog = dogRepository.findDetailById(id)
                .orElseThrow(() -> new EntityNotFoundException("Собака с id=" + id + " не найдена"));
        return DogResponseDto.from(dog);
    }

    @Transactional(readOnly = true)
    public List<DogListDto> findByBreed(String breed) {
        return dogRepository.findByBreed(breed).stream()
                .map(DogListDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DogListDto> findYoungerThan(LocalDate date) {
        return dogRepository.findYoungerThanWithLitter(date).stream()
                .map(DogListDto::from)
                .toList();
    }

    // динамический поиск через Criteria API: в where добавляются только заполненные фильтры
    @Transactional(readOnly = true)
    public List<DogListDto> search(DogSearchCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Dog> query = cb.createQuery(Dog.class);
        Root<Dog> dog = query.from(Dog.class);

        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.hasText(criteria.breed())) {
            predicates.add(cb.equal(dog.get("breed"), criteria.breed()));
        }
        if (criteria.gender() != null) {
            predicates.add(cb.equal(dog.get("gender"), criteria.gender()));
        }
        if (criteria.bornAfter() != null) {
            predicates.add(cb.greaterThanOrEqualTo(dog.get("birthDate"), criteria.bornAfter()));
        }
        if (criteria.bornBefore() != null) {
            predicates.add(cb.lessThanOrEqualTo(dog.get("birthDate"), criteria.bornBefore()));
        }
        if (StringUtils.hasText(criteria.nameContains())) {
            predicates.add(cb.like(cb.lower(dog.get("name")),
                    "%" + criteria.nameContains().toLowerCase() + "%"));
        }

        query.where(predicates.toArray(Predicate[]::new));
        query.orderBy(cb.asc(dog.get("name")));

        return entityManager.createQuery(query).getResultList().stream()
                .map(DogListDto::from)
                .toList();
    }

    @Transactional
    public DogResponseDto update(Long id, DogUpdateDto dto) {
        Dog dog = dogRepository.findDetailById(id)
                .orElseThrow(() -> new EntityNotFoundException("Собака с id=" + id + " не найдена"));
        // @Version: отклоняем устаревшее обновление (запись изменил другой пользователь)
        if (dog.getVersion() != dto.version()) {
            throw new ConflictException(
                    "Собака была изменена другим пользователем, актуальная версия: " + dog.getVersion());
        }
        dog.setName(dto.name());
        dog.setBreed(dto.breed());
        dog.setGender(dto.gender());
        // flush до маппинга в DTO, чтобы в ответе была уже инкрементированная версия
        entityManager.flush();
        return DogResponseDto.from(dog);
    }
}
