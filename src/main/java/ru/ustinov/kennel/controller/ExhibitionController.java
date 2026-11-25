package ru.ustinov.kennel.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ustinov.kennel.dto.ExhibitionCreateDto;
import ru.ustinov.kennel.dto.ExhibitionResponseDto;
import ru.ustinov.kennel.service.ExhibitionService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    public ExhibitionController(ExhibitionService exhibitionService) {
        this.exhibitionService = exhibitionService;
    }

    @PostMapping
    public ResponseEntity<ExhibitionResponseDto> create(@Valid @RequestBody ExhibitionCreateDto dto) {
        ExhibitionResponseDto created = exhibitionService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ExhibitionResponseDto getById(@PathVariable Long id) {
        return exhibitionService.getById(id);
    }

    // GET /api/exhibitions/by-city?city=Москва — derived query
    @GetMapping("/by-city")
    public List<ExhibitionResponseDto> byCity(@RequestParam String city) {
        return exhibitionService.findByCity(city);
    }
}
