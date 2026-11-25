package ru.ustinov.kennel.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ustinov.kennel.dto.ParticipationCreateDto;
import ru.ustinov.kennel.dto.ParticipationResponseDto;
import ru.ustinov.kennel.dto.PodiumDto;
import ru.ustinov.kennel.service.ParticipationService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/participations")
public class ParticipationController {

    private final ParticipationService participationService;

    public ParticipationController(ParticipationService participationService) {
        this.participationService = participationService;
    }

    // регистрация собаки на выставку (связка N:M с оценкой и местом)
    @PostMapping
    public ResponseEntity<ParticipationResponseDto> register(@Valid @RequestBody ParticipationCreateDto dto) {
        ParticipationResponseDto created = participationService.register(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    // GET /api/participations/by-dog-name?name=Рекс — derived query с обходом связи
    @GetMapping("/by-dog-name")
    public List<ParticipationResponseDto> byDogName(@RequestParam String name) {
        return participationService.byDogName(name);
    }

    // GET /api/participations/results?exhibitionId=1 — JPQL + join fetch (протокол выставки)
    @GetMapping("/results")
    public List<ParticipationResponseDto> results(@RequestParam Long exhibitionId) {
        return participationService.results(exhibitionId);
    }

    // GET /api/participations/podium?exhibitionId=1 — нативный SQL (призёры 1-3)
    @GetMapping("/podium")
    public List<PodiumDto> podium(@RequestParam Long exhibitionId) {
        return participationService.podium(exhibitionId);
    }
}
