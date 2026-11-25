package ru.ustinov.kennel.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exhibitions")
public class Exhibition {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exhibition_gen")
    @SequenceGenerator(name = "exhibition_gen", sequenceName = "exhibition_seq", allocationSize = 50)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @OneToMany(mappedBy = "exhibition", fetch = FetchType.LAZY)
    private List<Participation> participations = new ArrayList<>();

    protected Exhibition() {
    }

    public Exhibition(String title, String city, LocalDate eventDate) {
        this.title = title;
        this.city = city;
        this.eventDate = eventDate;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCity() {
        return city;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public List<Participation> getParticipations() {
        return participations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exhibition other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
