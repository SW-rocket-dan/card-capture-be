package app.cardcapture.auth.jwt.service;

import app.cardcapture.auth.jwt.config.JwtConfig;
import app.cardcapture.auth.jwt.config.JwtConfigStub;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.stereotype.Component;

import static org.mockito.Mockito.*;

@Component
public class JwtComponentStub extends JwtComponent {

    private final JWTVerifier jwtVerifier;
    private final Algorithm jwtHashAlgorithm;
    private final JwtConfig jwtConfig;

    private JwtComponentStub(JwtConfig jwtConfig) {
        super(jwtConfig);
        this.jwtConfig = jwtConfig;
        this.jwtHashAlgorithm = Algorithm.HMAC256(jwtConfig.getSecret());

        this.jwtVerifier = mock(JWTVerifier.class);
        when(this.jwtVerifier.verify(anyString())).thenAnswer(invocation -> {
            String token = invocation.getArgument(0);
            DecodedJWT decodedJWT = JWT.decode(token);
            if ("valid_token".equals(token)) {
                return decodedJWT;
            } else {
                throw new RuntimeException("Invalid JWT token");
            }
        });
    }

    public static JwtComponent createStub() {
        JwtConfig jwtConfig = JwtConfigStub.createStub();
        return new JwtComponentStub(jwtConfig);
    }

    @Override
    public Algorithm getJwtHashAlgorithm() {
        return this.jwtHashAlgorithm;
    }

    @Override
    public JWTVerifier getJwtVerifier() {
        return this.jwtVerifier;
    }
}
