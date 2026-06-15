package com.app.ecom.mono.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UserRole userRole;
    private Address address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
