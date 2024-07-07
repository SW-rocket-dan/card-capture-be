package app.cardcapture.auth.jwt.service;

import app.cardcapture.auth.jwt.config.JwtConfig;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Date;

import static org.mockito.Mockito.*;

public class JwtComponentStub extends JwtComponent {

    public JwtComponentStub(JwtConfig jwtConfig) {
        super(jwtConfig);
    }

    public static JwtComponent createStub() {
        // JwtConfig 목 객체 생성 및 설정
        JwtConfig jwtConfig = mock(JwtConfig.class);
        when(jwtConfig.getSecret()).thenReturn("test-secret");
        when(jwtConfig.getIssuer()).thenReturn("test-issuer");
        when(jwtConfig.getExpirationDate(any(Date.class))).thenAnswer(invocation -> {
            Date now = invocation.getArgument(0);
            return new Date(now.getTime() + 3600 * 1000); // 1시간 후로 설정
        });

        // Algorithm 및 JWTVerifier 목 객체 생성 및 설정
        Algorithm jwtHashAlgorithm = Algorithm.HMAC256(jwtConfig.getSecret());
        JWTVerifier jwtVerifier = mock(JWTVerifier.class);
        when(jwtVerifier.verify(anyString())).thenAnswer(invocation -> {
            String token = invocation.getArgument(0);
            DecodedJWT decodedJWT = JWT.decode(token);
            return decodedJWT;
        });

        // JwtComponent 객체에 목 객체 주입
        return new JwtComponent(jwtConfig) {
            @Override
            public Algorithm getJwtHashAlgorithm() {
                return jwtHashAlgorithm;
            }

            @Override
            public JWTVerifier getJwtVerifier() {
                return jwtVerifier;
            }
        };
    }
}