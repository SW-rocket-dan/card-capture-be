package app.cardcapture.user.service;

import app.cardcapture.user.domain.User;
import app.cardcapture.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException());
    }

    public User createUser(String email, String name) {
        User user = User.builder()
                .email(email)
                .name(name)
                .build();
        return userRepository.save(user);
    }
}