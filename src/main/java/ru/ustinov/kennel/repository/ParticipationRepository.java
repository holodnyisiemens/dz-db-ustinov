package ru.ustinov.kennel.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ustinov.kennel.entity.Participation;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    // derived query с обходом связи dog -> name + @EntityGraph для LAZY-связей
    @EntityGraph(attributePaths = {"dog", "exhibition"})
    List<Participation> findByDogName(String name);

    // JPQL (без nativeQuery): протокол результатов выставки, связи подгружаются join fetch
    @Query("SELECT p FROM Participation p JOIN FETCH p.dog JOIN FETCH p.exhibition " +
            "WHERE p.exhibition.id = :exhibitionId ORDER BY p.place")
    List<Participation> findResultsByExhibition(Long exhibitionId);

    // чистый SQL (nativeQuery = true): призёры (места 1-3)
    @Query(value = """
            SELECT p.place, d.name, d.breed, p.score
            FROM participations p
            JOIN dogs d ON d.id = p.dog_id
            WHERE p.exhibition_id = :exhibitionId AND p.place <= 3
            ORDER BY p.place
            """, nativeQuery = true)
    List<Object[]> findPodiumNative(Long exhibitionId);
}
