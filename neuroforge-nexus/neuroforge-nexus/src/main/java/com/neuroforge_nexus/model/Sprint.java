package com.neuroforge_nexus.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sprints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer tasksCount;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false)
    private String status; // Active, Planning, Completed

    private String goal;

    private Integer daysLeft;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
