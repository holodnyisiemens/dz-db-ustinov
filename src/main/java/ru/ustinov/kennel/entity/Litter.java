package ru.ustinov.kennel.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "litters")
public class Litter {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "litter_gen")
    @SequenceGenerator(name = "litter_gen", sequenceName = "litter_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    // обратная сторона двусторонней связи 1:N; собаки — составная часть приплода:
    // каскад PERSIST/MERGE (без ALL) и удаление «сирот» при удалении из коллекции
    @OneToMany(mappedBy = "litter",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Dog> dogs = new ArrayList<>();

    protected Litter() {
    }

    public Litter(String name, LocalDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }

    // вспомогательные методы: синхронизируют обе стороны двусторонней связи
    public void addDog(Dog dog) {
        dogs.add(dog);
        dog.setLitter(this);
    }

    public void removeDog(Dog dog) {
        dogs.remove(dog);
        dog.setLitter(null);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public List<Dog> getDogs() {
        return dogs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Litter other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
