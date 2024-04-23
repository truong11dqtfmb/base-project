package com.dqt.security.service;

import com.dqt.security.entity.RefreshToken;
import com.dqt.security.exception.TokenRefreshException;
import com.dqt.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-token.expiration}")
    private long JWT_REFRESH_EXPIRATION;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(String email) {
        RefreshToken refreshToken = RefreshToken.builder()
                .email(email)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(JWT_REFRESH_EXPIRATION))
                .build();
        RefreshToken save = refreshTokenRepository.save(refreshToken);
        return save;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new login request");
        }
        return token;
    }

    public void deleteByEmail(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }


}
