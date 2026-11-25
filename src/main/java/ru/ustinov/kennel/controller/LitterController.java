package ru.ustinov.kennel.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ustinov.kennel.dto.DogCreateDto;
import ru.ustinov.kennel.dto.DogResponseDto;
import ru.ustinov.kennel.dto.LitterCreateDto;
import ru.ustinov.kennel.dto.LitterResponseDto;
import ru.ustinov.kennel.service.LitterService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/litters")
public class LitterController {

    private final LitterService litterService;

    public LitterController(LitterService litterService) {
        this.litterService = litterService;
    }

    @PostMapping
    public ResponseEntity<LitterResponseDto> create(@Valid @RequestBody LitterCreateDto dto) {
        LitterResponseDto created = litterService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public LitterResponseDto getById(@PathVariable Long id) {
        return litterService.getById(id);
    }

    // GET /api/litters/big?from=2023-01-01&minDogs=3 — нативный SQL
    @GetMapping("/big")
    public List<LitterResponseDto> bigLitters(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam int minDogs) {
        return litterService.findBigLitters(from, minDogs);
    }

    // создать собаку сразу внутри приплода (синхронизация сторон связи, каскад PERSIST)
    @PostMapping("/{id}/dogs")
    public ResponseEntity<DogResponseDto> addDog(@PathVariable Long id, @Valid @RequestBody DogCreateDto dto) {
        DogResponseDto created = litterService.addDog(id, dto);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/dogs/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    // удалить собаку из приплода: orphanRemoval=true удалит её и из таблицы dogs
    @DeleteMapping("/{id}/dogs/{dogId}")
    public ResponseEntity<Void> removeDog(@PathVariable Long id, @PathVariable Long dogId) {
        litterService.removeDog(id, dogId);
        return ResponseEntity.noContent().build();
    }
}
