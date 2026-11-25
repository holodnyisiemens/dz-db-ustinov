package ru.ustinov.kennel.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ustinov.kennel.entity.Exhibition;

import java.util.List;
import java.util.Optional;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    // derived query
    List<Exhibition> findByCity(String city);

    // @EntityGraph: participations + их собаки (две LAZY-связи) подгружаются одним запросом
    @EntityGraph(attributePaths = {"participations", "participations.dog"})
    Optional<Exhibition> findDetailById(Long id);
}
