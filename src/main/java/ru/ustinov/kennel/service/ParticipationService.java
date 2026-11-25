package ru.ustinov.kennel.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.kennel.dto.ParticipationCreateDto;
import ru.ustinov.kennel.dto.ParticipationResponseDto;
import ru.ustinov.kennel.dto.PodiumDto;
import ru.ustinov.kennel.entity.Dog;
import ru.ustinov.kennel.entity.Exhibition;
import ru.ustinov.kennel.entity.Participation;
import ru.ustinov.kennel.repository.DogRepository;
import ru.ustinov.kennel.repository.ExhibitionRepository;
import ru.ustinov.kennel.repository.ParticipationRepository;

import java.util.List;

@Service
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final DogRepository dogRepository;
    private final ExhibitionRepository exhibitionRepository;

    public ParticipationService(ParticipationRepository participationRepository,
                                DogRepository dogRepository,
                                ExhibitionRepository exhibitionRepository) {
        this.participationRepository = participationRepository;
        this.dogRepository = dogRepository;
        this.exhibitionRepository = exhibitionRepository;
    }

    @Transactional
    public ParticipationResponseDto register(ParticipationCreateDto dto) {
        Dog dog = dogRepository.findById(dto.dogId())
                .orElseThrow(() -> new EntityNotFoundException("Собака с id=" + dto.dogId() + " не найдена"));
        Exhibition exhibition = exhibitionRepository.findById(dto.exhibitionId())
                .orElseThrow(() -> new EntityNotFoundException("Выставка с id=" + dto.exhibitionId() + " не найдена"));
        Participation participation = new Participation(dog, exhibition, dto.score(), dto.place());
        return ParticipationResponseDto.from(participationRepository.save(participation));
    }

    @Transactional(readOnly = true)
    public List<ParticipationResponseDto> results(Long exhibitionId) {
        // JPQL с join fetch: протокол выставки со связями одним запросом
        return participationRepository.findResultsByExhibition(exhibitionId).stream()
                .map(ParticipationResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ParticipationResponseDto> byDogName(String name) {
        return participationRepository.findByDogName(name).stream()
                .map(ParticipationResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PodiumDto> podium(Long exhibitionId) {
        // чистый SQL, результат маппим из Object[] в DTO
        return participationRepository.findPodiumNative(exhibitionId).stream()
                .map(row -> new PodiumDto((Integer) row[0], (String) row[1], (String) row[2], (Integer) row[3]))
                .toList();
    }
}
