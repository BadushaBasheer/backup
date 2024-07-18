package com.socialmedia.socialmedia.repositories;

import com.socialmedia.socialmedia.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM Token t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
}
