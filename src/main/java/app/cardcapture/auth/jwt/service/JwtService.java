package app.cardcapture.auth.jwt.service;

import app.cardcapture.auth.jwt.dto.JwtDto;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public JwtDto publish(String id) {
        return new JwtDto("auth code test");
    }
}
