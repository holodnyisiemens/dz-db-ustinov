package ru.ustinov.kennel.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ustinov.kennel.dto.DogCreateDto;
import ru.ustinov.kennel.dto.DogListDto;
import ru.ustinov.kennel.dto.DogResponseDto;
import ru.ustinov.kennel.dto.DogSearchCriteria;
import ru.ustinov.kennel.dto.DogUpdateDto;
import ru.ustinov.kennel.entity.Gender;
import ru.ustinov.kennel.service.DogService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dogs")
public class DogController {

    private final DogService dogService;

    public DogController(DogService dogService) {
        this.dogService = dogService;
    }

    @PostMapping
    public ResponseEntity<DogResponseDto> create(@Valid @RequestBody DogCreateDto dto) {
        DogResponseDto created = dogService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public DogResponseDto getById(@PathVariable Long id) {
        return dogService.getById(id);
    }

    @PutMapping("/{id}")
    public DogResponseDto update(@PathVariable Long id, @Valid @RequestBody DogUpdateDto dto) {
        return dogService.update(id, dto);
    }

    // GET /api/dogs/by-breed?breed=Лабрадор — derived query
    @GetMapping("/by-breed")
    public List<DogListDto> byBreed(@RequestParam String breed) {
        return dogService.findByBreed(breed);
    }

    // GET /api/dogs/younger-than?date=2023-01-01 — JPQL + join fetch
    @GetMapping("/younger-than")
    public List<DogListDto> youngerThan(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return dogService.findYoungerThan(date);
    }

    // GET /api/dogs/search?breed=...&gender=MALE&bornAfter=...&bornBefore=...&nameContains=...
    // все параметры необязательные — Criteria API собирает where только из заполненных
    @GetMapping("/search")
    public List<DogListDto> search(@RequestParam(required = false) String breed,
                                   @RequestParam(required = false) Gender gender,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bornAfter,
                                   @RequestParam(required = false)
                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bornBefore,
                                   @RequestParam(required = false) String nameContains) {
        return dogService.search(new DogSearchCriteria(breed, gender, bornAfter, bornBefore, nameContains));
    }
}
