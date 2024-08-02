package app.cardcapture.auth.jwt.service;

import app.cardcapture.auth.jwt.config.JwtConfig;
import app.cardcapture.auth.jwt.domain.entity.TokenBlacklist;
import app.cardcapture.auth.jwt.repository.TokenBlacklistRepository;
import app.cardcapture.common.exception.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final static String TOKEN_ALREADY_BLACKLISTED = "이미 블랙리스트에 추가된 토큰입니다.";
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtConfig jwtConfig;

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }

    public void addToBlacklist(String token) {
        String refinedToken = token.replace("Bearer ", "");

        if (isTokenBlacklisted(refinedToken)) {
            throw new BusinessLogicException(TOKEN_ALREADY_BLACKLISTED, HttpStatus.BAD_REQUEST);
        }

        TokenBlacklist tokenBlacklist = new TokenBlacklist();
        tokenBlacklist.setToken(refinedToken);
        tokenBlacklist.setExpiryDate(jwtConfig.getExpirationDate(LocalDateTime.now()));
        tokenBlacklistRepository.save(tokenBlacklist);
    }
}