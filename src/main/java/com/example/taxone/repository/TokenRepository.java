package com.example.taxone.repository;

import com.example.taxone.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    @Query("""
        SELECT t FROM Token t INNER JOIN User u ON t.user.id = u.id
        WHERE u.id = :userId AND (t.isExpired = false AND t.isRevoked = false)""")
    List<Token> findAllValidTokensByUserId(UUID userId);

    Optional<Token> findByToken(String token);
}