package com.example.taxone.repository;

import com.example.taxone.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabelRepository extends JpaRepository<Label, UUID> {
    List<Label> findAllByWorkspaceId(UUID id);
}
