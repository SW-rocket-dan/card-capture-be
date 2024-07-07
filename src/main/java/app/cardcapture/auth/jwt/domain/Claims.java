package app.cardcapture.auth.jwt.domain;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;

import java.util.Date;

@Getter
public class Claims {
    private String id;
    private String[] roles;
    private Date issuedAt;
    private Date expiresAt;
    private String issuer;

    private Claims() {}

    public Claims(DecodedJWT decodedJWT) {
        this.id = decodedJWT.getClaim("id").asString();
        this.roles = decodedJWT.getClaim("roles").asArray(String.class);
        this.issuedAt = decodedJWT.getIssuedAt();
        this.expiresAt = decodedJWT.getExpiresAt();
        this.issuer = decodedJWT.getIssuer();
    }

    public static Claims of(String id, String role, String issuer) {
        Claims claims = new Claims();

        claims.id = id;
        claims.roles = new String[]{role};
        claims.issuedAt = new Date();
        claims.expiresAt = null;
        claims.issuer = issuer;

        return claims;
    }
}