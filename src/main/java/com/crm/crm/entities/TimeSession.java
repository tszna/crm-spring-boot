package com.crm.crm.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Data
@Table(name = "time_sessions")
public class TimeSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship with users
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalTime elapsedTime;
    private LocalTime fullElapsedTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
