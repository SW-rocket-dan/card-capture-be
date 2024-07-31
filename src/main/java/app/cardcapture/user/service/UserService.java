    package app.cardcapture.user.service;

    import app.cardcapture.auth.jwt.domain.Claims;
    import app.cardcapture.auth.jwt.service.JwtComponent;
    import app.cardcapture.common.exception.BusinessLogicException;
    import app.cardcapture.user.domain.entity.User;
    import app.cardcapture.user.dto.UserDto;
    import app.cardcapture.user.repository.UserRepository;
    import lombok.AllArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;

    import java.util.Optional;

    @Service
    @AllArgsConstructor
    public class UserService {
        private static final String USER_INFO_RETRIEVAL_ERROR = "Failed to retrieve user info";
        private final UserRepository userRepository;
        private final JwtComponent jwtComponent;

        public UserDto findUserById(Long id) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new BusinessLogicException(USER_INFO_RETRIEVAL_ERROR, HttpStatus.NOT_FOUND));
            return UserDto.from(user);
        }

        public User save(UserDto userDto) {
            return userRepository.save(userDto.toEntity());
        }

        public UserDto findUserByAccessToken(String accessToken) {
            Claims claims = jwtComponent.verify(accessToken);
            Long id = claims.getId();

            User user = userRepository.findById(id)
                    .orElseThrow(() -> new BusinessLogicException(USER_INFO_RETRIEVAL_ERROR, HttpStatus.NOT_FOUND));
            return UserDto.from(user);
        }

        public Optional<User> findByGoogleId(String googleId) {
            return userRepository.findByGoogleId(googleId);
        }
    }