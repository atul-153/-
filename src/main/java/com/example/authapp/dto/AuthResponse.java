package com.example.authapp.dto;

public record AuthResponse(String token, String type, String email, String role) {
}
