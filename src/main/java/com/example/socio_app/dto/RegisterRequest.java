package com.example.socio_app.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "username not be empty")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,15}$",message = "username {min:3 to max:15}char, only letters,numbers,hypens and underscore")
    private String username;
    @NotBlank(message = "email cannot be empty")

    @Pattern(   regexp = "^[a-z0-9._%+-]+@gmail\\.com$",message = "invalid email format")
    private String email;
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[@!$%*?&])[A-Za-z\\d@!$%*?&]{8,}$",
            message = "Password must have at least 8 characters, including at least one uppercase letter and one special character (@!$%*?&)"
    )
    private String password;


}
