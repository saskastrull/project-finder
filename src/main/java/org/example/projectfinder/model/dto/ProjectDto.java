package org.example.projectfinder.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.projectfinder.model.entity.Keyword;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {

    private Long projectId;

    private LocalDate startDate;

    private LocalDate endDate;

    @NotBlank(message = "Description is required")
    private String description;

    @Size(max = 100, message = "Character limit is set to 100")
    private String location;

    private String url;

    private Set<Keyword> keywords;
}
