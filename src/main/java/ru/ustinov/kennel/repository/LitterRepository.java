package ru.ustinov.kennel.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ustinov.kennel.entity.Litter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LitterRepository extends JpaRepository<Litter, Long> {

    // ленивая коллекция dogs подгружается через @EntityGraph
    @EntityGraph(attributePaths = "dogs")
    Optional<Litter> findDetailById(Long id);

    // чистый SQL (nativeQuery = true)
    @Query(value = """
            SELECT l.*
            FROM litters l
            WHERE l.birth_date >= :from
              AND (SELECT COUNT(*) FROM dogs d WHERE d.litter_id = l.id) >= :minDogs
            ORDER BY l.birth_date
            """, nativeQuery = true)
    List<Litter> findWithAtLeastDogsBornAfter(LocalDate from, int minDogs);
}
