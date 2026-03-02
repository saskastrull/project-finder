package org.example.projectfinder.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeywordDto {

    private Long id;
    @NotBlank(message = "Keyword is required")
    @Size(max = 50, message = "Character limit is set to 50")
    private String keyword;
}