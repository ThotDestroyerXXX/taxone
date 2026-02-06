package com.example.taxone.repository;

import com.example.taxone.entity.ProjectInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface ProjectInvitationRepository extends JpaRepository<ProjectInvitation, UUID> {

}
