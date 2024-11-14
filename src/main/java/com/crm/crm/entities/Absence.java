package com.crm.crm.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "absences")
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship with users
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull(message = "Data początkowa jest wymagana")
    private LocalDate startDate;

    @NotNull(message = "Data końcowa jest wymagana")
    private LocalDate endDate;

    @NotNull(message = "Powód jest wymagany")
    @Size(max = 255)
    private String reason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
