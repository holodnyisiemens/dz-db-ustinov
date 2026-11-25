package ru.ustinov.kennel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ustinov.kennel.entity.Dog;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DogRepository extends JpaRepository<Dog, Long> {

    // derived query: Spring Data JPA строит запрос по имени метода
    List<Dog> findByBreed(String breed);

    // JPQL (без nativeQuery): ленивые связи подгружаются через join fetch
    @Query("SELECT d FROM Dog d LEFT JOIN FETCH d.litter LEFT JOIN FETCH d.passport WHERE d.id = :id")
    Optional<Dog> findDetailById(Long id);

    @Query("SELECT d FROM Dog d LEFT JOIN FETCH d.litter WHERE d.birthDate > :date")
    List<Dog> findYoungerThanWithLitter(LocalDate date);
}
