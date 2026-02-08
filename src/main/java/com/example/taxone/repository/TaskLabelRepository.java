package com.example.taxone.repository;


import com.example.taxone.entity.TaskLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskLabelRepository extends JpaRepository<TaskLabel, UUID> {
    void deleteAllByLabelId(UUID labelUUID);
}
