package app.cardcapture.auth.jwt.service;

import app.cardcapture.auth.jwt.config.JwtConfig;
import app.cardcapture.auth.jwt.domain.entity.TokenBlacklist;
import app.cardcapture.auth.jwt.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtConfig jwtConfig;

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }

    public void addToBlacklist(String token) {
        String refinedToken = token.replace("Bearer ", "");

        if (isTokenBlacklisted(refinedToken)) {
            return;
        }

        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        tokenBlacklist.setToken(refinedToken);
        tokenBlacklist.setExpiryDate(jwtConfig.getAccessExpirationDate(LocalDateTime.now()));
        tokenBlacklistRepository.save(tokenBlacklist);
    }
}