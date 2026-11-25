package ru.ustinov.kennel.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dogs")
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dog_gen")
    @SequenceGenerator(name = "dog_gen", sequenceName = "dog_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String breed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    // данные собаки могут одновременно редактировать несколько пользователей
    @Version
    private long version;

    // владеющая сторона двусторонней связи 1:N «приплод — собака» (внешний ключ здесь)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "litter_id")
    private Litter litter;

    // обратная сторона 1:1: внешний ключ dog_id лежит в veterinary_passports
    @OneToOne(mappedBy = "dog", fetch = FetchType.LAZY)
    private VeterinaryPassport passport;

    @OneToMany(mappedBy = "dog", fetch = FetchType.LAZY)
    private List<Participation> participations = new ArrayList<>();

    protected Dog() {
    }

    public Dog(String name, String breed, Gender gender, LocalDate birthDate) {
        this.name = name;
        this.breed = breed;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public long getVersion() {
        return version;
    }

    public Litter getLitter() {
        return litter;
    }

    public void setLitter(Litter litter) {
        this.litter = litter;
    }

    public VeterinaryPassport getPassport() {
        return passport;
    }

    public void setPassport(VeterinaryPassport passport) {
        this.passport = passport;
    }

    public List<Participation> getParticipations() {
        return participations;
    }

    // equals/hashCode по паре «тип + id», без коллекций
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dog other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
