package com.socialmedia.socialmedia.services.auth;

public interface TokenService {

    void removeExpiredTokens();
}
