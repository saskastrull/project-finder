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
public class AuthRequest {

    @NotBlank(message = "Username is required")
    @Size(max = 30, message = "Character limit is set to 30")
    private String username;
    @NotBlank(message = "Password is required")
    @Size(max = 30, message = "Character limit is set to 30")
    private String password;
}
