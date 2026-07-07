package com.neuroforge_nexus.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kafka_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String taskKey;

    @Column(nullable = false)
    private String eventType; // TASK_CREATED, TASK_MOVED, TASK_BLOCKED

    @Column(nullable = false, length = 1000)
    private String payload;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
