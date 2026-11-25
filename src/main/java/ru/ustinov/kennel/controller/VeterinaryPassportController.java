package ru.ustinov.kennel.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ustinov.kennel.dto.PassportCreateDto;
import ru.ustinov.kennel.dto.PassportResponseDto;
import ru.ustinov.kennel.service.VeterinaryPassportService;

import java.net.URI;

@RestController
@RequestMapping("/api/passports")
public class VeterinaryPassportController {

    private final VeterinaryPassportService passportService;

    public VeterinaryPassportController(VeterinaryPassportService passportService) {
        this.passportService = passportService;
    }

    @PostMapping
    public ResponseEntity<PassportResponseDto> create(@Valid @RequestBody PassportCreateDto dto) {
        PassportResponseDto created = passportService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    // GET /api/passports/number/AB-123 — derived query по бизнес-ключу
    @GetMapping("/number/{number}")
    public PassportResponseDto getByNumber(@PathVariable String number) {
        return passportService.getByNumber(number);
    }
}
