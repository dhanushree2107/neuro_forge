package com.neuroforge_nexus.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String status; // Active, Inactive, Completed

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private String description;
}
