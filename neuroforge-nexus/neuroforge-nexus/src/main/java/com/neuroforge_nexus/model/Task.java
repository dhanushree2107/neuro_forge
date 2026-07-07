package com.neuroforge_nexus.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String taskKey; // e.g. PAY-247

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false)
    private String status; // To Do, In Progress, Done

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne
    @JoinColumn(name = "sprint_id", nullable = false)
    private Sprint sprint;

    @Column(nullable = false)
    private boolean isBlocked;

    private String blockerReason;

    private String blockerStatus; // None, Pending, Escalated
}
