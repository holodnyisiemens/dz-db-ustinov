package ru.ustinov.kennel.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.kennel.dto.ExhibitionCreateDto;
import ru.ustinov.kennel.dto.ExhibitionResponseDto;
import ru.ustinov.kennel.entity.Exhibition;
import ru.ustinov.kennel.repository.ExhibitionRepository;

import java.util.List;

@Service
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;

    public ExhibitionService(ExhibitionRepository exhibitionRepository) {
        this.exhibitionRepository = exhibitionRepository;
    }

    @Transactional
    public ExhibitionResponseDto create(ExhibitionCreateDto dto) {
        Exhibition exhibition = new Exhibition(dto.title(), dto.city(), dto.eventDate());
        return ExhibitionResponseDto.summary(exhibitionRepository.save(exhibition));
    }

    @Transactional(readOnly = true)
    public ExhibitionResponseDto getById(Long id) {
        // findDetailById — @EntityGraph подгружает LAZY participations и их собак одним запросом
        Exhibition exhibition = exhibitionRepository.findDetailById(id)
                .orElseThrow(() -> new EntityNotFoundException("Выставка с id=" + id + " не найдена"));
        return ExhibitionResponseDto.from(exhibition);
    }

    @Transactional(readOnly = true)
    public List<ExhibitionResponseDto> findByCity(String city) {
        return exhibitionRepository.findByCity(city).stream()
                .map(ExhibitionResponseDto::summary)
                .toList();
    }
}
