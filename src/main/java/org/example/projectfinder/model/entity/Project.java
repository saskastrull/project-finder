package org.example.projectfinder.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Column(nullable = false)
    private String url;

    @ManyToMany
    @JoinTable(
            name = "project_keyword",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "keyword_id")
    )
    private Set<Keyword> keywords = new HashSet<>();

    public Project(LocalDate startDate, LocalDate endDate, String description, String location, String url) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.location = location;
        this.url = url;
    }
}
