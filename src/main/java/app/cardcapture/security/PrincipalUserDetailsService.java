package app.cardcapture.security;

import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User principal = userRepository.findUserAndUserRolesByUserId(Long.valueOf(id))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new PrincipalDetails(principal);
    }
}
