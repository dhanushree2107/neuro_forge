package com.neuroforge_nexus.repository;

import com.neuroforge_nexus.model.KafkaEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KafkaEventRepository extends JpaRepository<KafkaEvent, Long> {
    List<KafkaEvent> findAllByOrderByTimestampDesc();
}
