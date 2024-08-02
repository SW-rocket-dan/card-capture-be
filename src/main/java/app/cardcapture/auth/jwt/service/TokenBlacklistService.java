package app.cardcapture.auth.jwt.service;

import app.cardcapture.auth.jwt.domain.entity.TokenBlacklist;
import app.cardcapture.auth.jwt.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }

    public void addToBlacklist(String token) {
        String refinedToken = token.replace("Bearer ", "");

        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        tokenBlacklist.setToken(refinedToken);

        tokenBlacklistRepository.save(tokenBlacklist);
    }
}