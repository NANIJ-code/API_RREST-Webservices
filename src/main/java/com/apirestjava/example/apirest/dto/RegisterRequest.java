package com.apirestjava.example.apirest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Le champ 'username' est obligatoire")
    @Size(min = 3, max = 50, message = "'username' doit contenir entre 3 et 50 caractères")
    private String username;

    @NotBlank(message = "Le champ 'email' est obligatoire")
    @Email(message = "'email' n'est pas une adresse valide")
    @Size(max = 150, message = "'email' est trop long")
    private String email;

    @NotBlank(message = "Le champ 'password' est obligatoire")
    @Size(min = 8, max = 72, message = "'password' doit contenir entre 8 et 72 caractères")
    private String password;

    public RegisterRequest() {
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}