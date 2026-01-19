package com.SecureBankingApi.application.usecases;

import com.SecureBankingApi.domain.user.enums.UserRole;
import com.SecureBankingApi.domain.user.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegisterUserResponse {
    private final UUID id;
    private final String fullName;
    private final String cpf;
    private final String email;
    private final UserRole role;
    private final UserStatus status;
    private final LocalDateTime createdAt;

    public RegisterUserResponse(UUID id, String fullName, String cpf, String email, UserRole role, UserStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.cpf = cpf;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
