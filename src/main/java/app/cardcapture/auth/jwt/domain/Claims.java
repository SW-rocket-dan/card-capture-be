package app.cardcapture.auth.jwt.domain;

import app.cardcapture.common.utils.TimeUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
public class Claims { //TODO: Access Token, Refresh Token 용 Claims 구분하기(NullpointerException 방지)
    private Long id;
    private String[] roles;
    private Date issuedAt;
    private Date expiresAt;
    private String issuer;
    private Date createdAt;

    private Claims() {}

    public Claims(DecodedJWT decodedJWT) {
        this.id = decodedJWT.getClaim("id").asLong();
        this.roles = decodedJWT.getClaim("roles").asArray(String.class);
        this.issuedAt = decodedJWT.getIssuedAt();
        this.expiresAt = decodedJWT.getExpiresAt();
        this.issuer = decodedJWT.getIssuer();
        this.createdAt = decodedJWT.getClaim("created_at").asDate();
    }

    public static Claims of(Long id, String role, String issuer, Date createdAt) {
        Claims claims = new Claims();

        claims.id = id;
        claims.roles = new String[]{role};
        claims.issuedAt = new Date();
        claims.expiresAt = null;
        claims.issuer = issuer;
        claims.createdAt = createdAt;

        return claims;
    }

    public LocalDateTime getLoalDateCreatedAt() {
        return TimeUtils.toLocalDateTime(createdAt);
    }
}