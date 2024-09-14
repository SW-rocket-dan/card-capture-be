package app.cardcapture.user.service;

import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.business.domain.NewUserProductCategory;
import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import app.cardcapture.payment.business.repository.UserProductCategoryRepository;
import app.cardcapture.payment.common.service.PaymentCommonService;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.dto.UserDto;
import app.cardcapture.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private static final String USER_INFO_RETRIEVAL_ERROR = "Failed to retrieve user info";
    private final PaymentCommonService paymentCommonService;
    private final UserProductCategoryRepository userProductCategoryRepository;
    private final UserRepository userRepository;

    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(USER_INFO_RETRIEVAL_ERROR, HttpStatus.NOT_FOUND));
        return UserDto.from(user);
    }

    @Transactional
    public User save(UserDto userDto) {
        User savedUser = userRepository.save(userDto.toEntity());

        NewUserProductCategory newUserProductCategory = NewUserProductCategory.FREE_AI_POSTER_PRODUCTION_TICKET;
        paymentCommonService.saveUserProductCategory(newUserProductCategory.getProductCategory(), newUserProductCategory.getCount(), savedUser);

        return savedUser;
    }

    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }

    public List<UserProductCategory> getUserProductCategories(User user) {

        return userProductCategoryRepository.findByUser(user);
    }
}