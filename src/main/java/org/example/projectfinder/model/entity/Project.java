package org.example.projectfinder.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "url")
        }
)
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
    private int hours; // speed percentage
    @Column(nullable = false)
    private String origin;
    @Column(nullable = false, unique = true)
    private String url;

    public Project(LocalDate startDate, LocalDate endDate, String description, String location,
                   LocalDate deadline, int hours, String origin, String url) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.location = location;
        this.deadline = deadline;
        this.hours = hours;
        this.origin = origin;
        this.url = url;
    }
}
