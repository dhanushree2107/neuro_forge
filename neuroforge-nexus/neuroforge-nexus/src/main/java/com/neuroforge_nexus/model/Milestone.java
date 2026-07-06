package com.neuroforge_nexus.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "milestones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private String status; // Active, Completed, Pending

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
