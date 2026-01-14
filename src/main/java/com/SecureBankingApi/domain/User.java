package com.SecureBankingApi.domain;

import com.SecureBankingApi.domain.valueObjects.CPF;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID id;
    private String fullName;
    private final CPF cpf;

    private String email;
    private String passwordHash;

    private UserRole role;
    private UserStatus status;



    private  LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User(String email,
                 String fullName,
                 CPF cpf,
                 String passwordHash,
                 UserRole role) {

        this.id = null;
        this.email = email;
        this.fullName = fullName;
        this.cpf = cpf;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static User create(String email,
                              String fullName,
                              CPF cpf,
                              String passwordHash,
                              UserRole role){

        if(email == null || email.isBlank()) throw new IllegalArgumentException("email cannot be null");
        if(email.length() < 3 || email.length() > 100) throw new IllegalArgumentException("Invalid size of email");
        if (!email.matches("^\\S+@\\S+\\.\\S+$")) throw new IllegalArgumentException("Invalid format of email");

        if(fullName == null || fullName.isBlank()) throw new IllegalArgumentException("name cannot be null");
        if(fullName.length() < 3 || fullName.length() > 100) throw new IllegalArgumentException("Invalid size of fullName");

        if(cpf == null) throw new IllegalArgumentException("cpf cannot be null");

        if(passwordHash == null || passwordHash.isBlank()) throw new IllegalArgumentException("password cannot be null");

        if(role == null) throw new IllegalArgumentException("role cannot be null");


        return new User(
                email,
                fullName,
                cpf,
                passwordHash,
                role);
    }

    public static User restore(
            UUID id,
            String fullName,
            CPF cpf,
            String email,
            String passwordHash,
            UserRole role,
            UserStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt){

        User user = new User(email, fullName, cpf, passwordHash, role);

        user.id = id;
        user.role = role;
        user.createdAt = createdAt;
        user.status = status;
        user.updatedAt = updatedAt;

        return user;
    }

    @Override
    public String toString() {
        return "User{" +
                "createdAt=" + createdAt +
                ", status=" + status +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", cpf=" + cpf +
                ", fullName='" + fullName + '\'' +
                ", id=" + id +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
