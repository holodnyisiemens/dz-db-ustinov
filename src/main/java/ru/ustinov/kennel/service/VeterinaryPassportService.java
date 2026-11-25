package ru.ustinov.kennel.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.kennel.dto.PassportCreateDto;
import ru.ustinov.kennel.dto.PassportResponseDto;
import ru.ustinov.kennel.entity.Dog;
import ru.ustinov.kennel.entity.VeterinaryPassport;
import ru.ustinov.kennel.exception.ConflictException;
import ru.ustinov.kennel.repository.DogRepository;
import ru.ustinov.kennel.repository.VeterinaryPassportRepository;

@Service
public class VeterinaryPassportService {

    private final VeterinaryPassportRepository passportRepository;
    private final DogRepository dogRepository;

    public VeterinaryPassportService(VeterinaryPassportRepository passportRepository,
                                     DogRepository dogRepository) {
        this.passportRepository = passportRepository;
        this.dogRepository = dogRepository;
    }

    @Transactional
    public PassportResponseDto create(PassportCreateDto dto) {
        Dog dog = dogRepository.findById(dto.dogId())
                .orElseThrow(() -> new EntityNotFoundException("Собака с id=" + dto.dogId() + " не найдена"));
        if (dog.getPassport() != null) {
            throw new ConflictException("У собакы с id=" + dto.dogId() + " уже есть ветеринарный паспорт");
        }
        VeterinaryPassport passport = new VeterinaryPassport(
                dto.passportNumber(), dto.issueDate(), dto.veterinarianName());
        // синхронизация обеих сторон связи 1:1
        passport.assignTo(dog);
        return PassportResponseDto.from(passportRepository.save(passport));
    }

    @Transactional(readOnly = true)
    public PassportResponseDto getByNumber(String passportNumber) {
        // findByPassportNumber — derived query, @EntityGraph подтягивает LAZY-связь dog
        VeterinaryPassport passport = passportRepository.findByPassportNumber(passportNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Ветеринарный паспорт с номером " + passportNumber + " не найден"));
        return PassportResponseDto.from(passport);
    }
}
