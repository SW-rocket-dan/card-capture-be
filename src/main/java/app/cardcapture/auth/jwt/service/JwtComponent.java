package app.cardcapture.auth.jwt.service;

import app.cardcapture.auth.jwt.config.JwtConfig;
import app.cardcapture.auth.jwt.domain.Claims;
import app.cardcapture.auth.jwt.exception.InvalidTokenException;
import app.cardcapture.auth.jwt.exception.TokenBlacklistedException;
import app.cardcapture.common.utils.TimeUtils;
import app.cardcapture.user.domain.Role;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.util.List;
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

    public JwtComponent(JwtConfig jwtConfig, TokenBlacklistService tokenBlacklistService, UserService userService) {
        this.jwtConfig = jwtConfig;
        this.jwtHashAlgorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        this.jwtVerifier = JWT.require(jwtHashAlgorithm).withIssuer(jwtConfig.getIssuer()).build();
        this.tokenBlacklistService = tokenBlacklistService;
        this.userService = userService;
    }

    public String createAccessToken(Long userId, Role role, Date createdAt) {
        return this.createAccessToken(Claims.of(userId, List.of(role), jwtConfig.getIssuer(), createdAt));
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

    private DecodedJWT verifyJWT(String token) {
        DecodedJWT decodedJWT;
        try {
            decodedJWT = jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new InvalidTokenException(INVALID_TOKEN);
        }
        return decodedJWT;
    } //TODO: 한번 더 던져서 JWTExpiredException 처리 => filter에서 처리해야함! (controllerhandler에서 하는게 아닌게 맞는지 postman으로테스트하기)

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