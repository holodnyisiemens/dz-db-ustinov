package ru.ustinov.kennel.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "participations",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_participation_dog_exhibition",
                columnNames = {"dog_id", "exhibition_id"}))
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "participation_gen")
    @SequenceGenerator(name = "participation_gen", sequenceName = "participation_seq", allocationSize = 50)
    private Long id;

    // связка N:M «собака ↔ выставка»: две @ManyToOne + собственные атрибуты
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dog_id", nullable = false)
    private Dog dog;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exhibition_id", nullable = false)
    private Exhibition exhibition;

    // оценка, выставленная экспертом
    @Column(nullable = false)
    private int score;

    // занятое место
    @Column(nullable = false)
    private int place;

    // результаты (оценку/место) могут одновременно вносить несколько судей
    @Version
    private long version;

    protected Participation() {
    }

    public Participation(Dog dog, Exhibition exhibition, int score, int place) {
        this.dog = dog;
        this.exhibition = exhibition;
        this.score = score;
        this.place = place;
    }

    public Long getId() {
        return id;
    }

    public Dog getDog() {
        return dog;
    }

    public Exhibition getExhibition() {
        return exhibition;
    }

    public int getScore() {
        return score;
    }

    public int getPlace() {
        return place;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Participation other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
