package app.cardcapture.auth.jwt.service;

import app.cardcapture.auth.jwt.config.JwtConfig;
import app.cardcapture.auth.jwt.domain.Claims;
import app.cardcapture.auth.jwt.dto.JwtResponseDto;
import app.cardcapture.auth.jwt.dto.RefreshTokenRequestDto;
import app.cardcapture.auth.jwt.exception.InvalidTokenException;
import app.cardcapture.auth.jwt.exception.TokenBlacklistedException;
import app.cardcapture.common.utils.TimeUtils;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.domain.entity.UserRole;
import app.cardcapture.user.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.time.ZoneId;
import java.util.Set;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
public class JwtComponent {

    private static final String BLACKLISTED_TOKEN = "Token is blacklisted";
    private static final String INVALID_TOKEN = "Token is invalid";
    private static final String EXPIRED_TOKEN = "Token is expired";
    private final JwtConfig jwtConfig;
    private final Algorithm jwtHashAlgorithm;
    private final JWTVerifier jwtVerifier;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserService userService;

    public JwtComponent(JwtConfig jwtConfig, TokenBlacklistService tokenBlacklistService,
        UserService userService) {
        this.jwtConfig = jwtConfig;
        this.jwtHashAlgorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        this.jwtVerifier = JWT.require(jwtHashAlgorithm).withIssuer(jwtConfig.getIssuer()).build();
        this.tokenBlacklistService = tokenBlacklistService;
        this.userService = userService;
    }

    public String createAccessToken(Long userId, Set<UserRole> role, Date createdAt) {
        return this.createAccessToken(
            Claims.of(
                userId,
                role.stream()
                    .map(userRole -> userRole.getRole().name())
                    .toList(),
                jwtConfig.getIssuer(), createdAt));
    }

    public String createAccessToken(Claims claims) {
        Date now = new Date();

        JWTCreator.Builder builder = JWT.create();
        builder.withIssuer(claims.getIssuer());
        builder.withIssuedAt(now);
        builder.withExpiresAt(jwtConfig.getAccessExpirationDate(now));
        builder.withClaim("id", claims.getId());
        builder.withClaim("roles", claims.getRoles());
        builder.withClaim("created_at", claims.getCreatedAt());

        return builder.sign(jwtHashAlgorithm);
    }

    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date expirationDate = jwtConfig.getRefreshExpirationDate(now);

        return JWT.create()
            .withIssuer(jwtConfig.getIssuer())
            .withIssuedAt(now)
            .withExpiresAt(expirationDate)
            .withClaim("id", userId)
            .sign(jwtHashAlgorithm);
    }

    public Claims verifyAccessToken(String token) {
        verifyBlacklisted(token);

        DecodedJWT decodedJWT = verifyJWT(token);
        Claims claims = new Claims(decodedJWT);

        verifyActualUser(claims);

        return claims;
    }

    public Claims verifyRefreshToken(String token) {
        DecodedJWT decodedJWT = verifyJWT(token);
        Claims claims = new Claims(decodedJWT);

        return claims;
    }

    public JwtResponseDto refreshJwt(RefreshTokenRequestDto refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.refreshToken();

        Claims claims = verifyRefreshToken(refreshToken);
        Long userId = Long.valueOf(claims.getId());

        User user = userService.findUserById(userId);
        Date userCreatedAt = TimeUtils.toDate(user.getCreatedAt());

        String newJwt = createAccessToken(userId, user.getRoles(), userCreatedAt);
        String newRefreshToken = createRefreshToken(userId);

        return new JwtResponseDto(newJwt, newRefreshToken);
    }

    public JwtResponseDto issueJwt(User user) {
        String jwt = createAccessToken(user.getId(), user.getRoles(),
            Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        String refreshToken = createRefreshToken(user.getId());

        return new JwtResponseDto(jwt, refreshToken);
    }

    private DecodedJWT verifyJWT(String token) {
        DecodedJWT decodedJWT;
        try {
            decodedJWT = jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException(INVALID_TOKEN);
        }
        return decodedJWT;
    }

    private void verifyBlacklisted(String token) {
        if (tokenBlacklistService.isTokenBlacklisted(token)) {
            throw new TokenBlacklistedException(BLACKLISTED_TOKEN);
        }
    }

    private void verifyActualUser(Claims claims) {
        User foundUserById = userService.findUserById(claims.getId());
        long foundUserSecond = TimeUtils.toEpochSecond(foundUserById.getCreatedAt());
        long claimsSecond = TimeUtils.toEpochSecond(claims.getLoalDateCreatedAt());

        if (foundUserSecond != claimsSecond) {
            throw new InvalidTokenException(INVALID_TOKEN);
        }
    }
}