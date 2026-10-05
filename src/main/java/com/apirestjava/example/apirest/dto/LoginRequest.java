package com.apirestjava.example.apirest.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Le champ 'username' est obligatoire")
    private String username;

    @NotBlank(message = "Le champ 'password' est obligatoire")
    private String password;

    public LoginRequest() {
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}