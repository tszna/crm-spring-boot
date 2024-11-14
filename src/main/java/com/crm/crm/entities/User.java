package com.crm.crm.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 191)
    private String name;

    @Column(nullable = false, unique = true, length = 191)
    private String email;

    private LocalDateTime emailVerifiedAt;

    @Column(nullable = false, length = 191)
    private String password;

    @Column(length = 100)
    private String rememberToken;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
