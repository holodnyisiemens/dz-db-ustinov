package ru.ustinov.kennel.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ustinov.kennel.entity.VeterinaryPassport;

import java.util.Optional;

public interface VeterinaryPassportRepository extends JpaRepository<VeterinaryPassport, Long> {

    // derived query по бизнес-ключу + сразу подтягиваем собаку (ленивая связь)
    @EntityGraph(attributePaths = "dog")
    Optional<VeterinaryPassport> findByPassportNumber(String passportNumber);
}
