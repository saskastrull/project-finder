package org.example.projectfinder.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;
    private LocalDate startDate;
    private LocalDate endDate;
    @Column(nullable = false)
    private String description;
    private String location;
    private LocalDate deadline;
    private String company;
    private int hours; // percentage
    @Column(nullable = false)
    private String url;

    @ManyToMany
    @JoinTable(
            name = "project_keyword",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    private Set<Keyword> keywords = new HashSet<>();

    public Project(LocalDate startDate, LocalDate endDate, String description, String location,
                   LocalDate deadline, String company, int hours, String url) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.location = location;
        this.deadline = deadline;
        this.company = company;
        this.hours = hours;
        this.url = url;
    }
}
