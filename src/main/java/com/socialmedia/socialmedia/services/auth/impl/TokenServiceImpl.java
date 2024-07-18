package com.socialmedia.socialmedia.services.auth.impl;

import com.socialmedia.socialmedia.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl {

    private final TokenRepository tokenRepository;

    @Scheduled(fixedRate = 60000)
    public void removeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteExpiredTokens(now);
    }

}
