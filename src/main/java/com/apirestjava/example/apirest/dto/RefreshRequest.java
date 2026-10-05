package com.apirestjava.example.apirest.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshRequest {

    @NotBlank(message = "Le champ 'refreshToken' est obligatoire")
    private String refreshToken;

    public RefreshRequest() {
    }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}