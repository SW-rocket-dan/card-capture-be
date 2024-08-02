package app.cardcapture.auth.jwt.service;

import app.cardcapture.auth.jwt.config.JwtConfig;
import app.cardcapture.auth.jwt.domain.Claims;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
public class JwtComponent {

    private final JwtConfig jwtConfig;
    private final Algorithm jwtHashAlgorithm;
    private final JWTVerifier jwtVerifier;

    public JwtComponent(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.jwtHashAlgorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        this.jwtVerifier = JWT.require(jwtHashAlgorithm).withIssuer(jwtConfig.getIssuer()).build();
    }

    public String create(Long userId, String role, Date createdAt) {
        return this.create(Claims.of(userId, role, jwtConfig.getIssuer(), createdAt));
    }

    public String create(Claims claims) {
        Date now = new Date();

        JWTCreator.Builder builder = JWT.create();
        builder.withIssuer(claims.getIssuer());
        builder.withIssuedAt(now);
        builder.withExpiresAt(jwtConfig.getExpirationDate(now));
        builder.withClaim("id", claims.getId());
        builder.withArrayClaim("roles", claims.getRoles());
        builder.withClaim("created_at", claims.getCreatedAt());

        return builder.sign(jwtHashAlgorithm);
    }

    public Claims verify(String token) {
        return new Claims(jwtVerifier.verify(token));

    }
}