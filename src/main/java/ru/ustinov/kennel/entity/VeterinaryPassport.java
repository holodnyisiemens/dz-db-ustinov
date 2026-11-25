package ru.ustinov.kennel.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "veterinary_passports")
public class VeterinaryPassport {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passport_gen")
    @SequenceGenerator(name = "passport_gen", sequenceName = "passport_seq", allocationSize = 50)
    private Long id;

    @Column(name = "passport_number", nullable = false, unique = true, length = 50)
    private String passportNumber;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "veterinarian_name", length = 120)
    private String veterinarianName;

    // владеющая сторона 1:1: внешний ключ dog_id уникален
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dog_id", nullable = false, unique = true)
    private Dog dog;

    protected VeterinaryPassport() {
    }

    public VeterinaryPassport(String passportNumber, LocalDate issueDate, String veterinarianName) {
        this.passportNumber = passportNumber;
        this.issueDate = issueDate;
        this.veterinarianName = veterinarianName;
    }

    // вспомогательный метод: синхронизирует обе стороны связи 1:1
    public void assignTo(Dog dog) {
        this.dog = dog;
        dog.setPassport(this);
    }

    public Long getId() {
        return id;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public String getVeterinarianName() {
        return veterinarianName;
    }

    public Dog getDog() {
        return dog;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VeterinaryPassport other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
