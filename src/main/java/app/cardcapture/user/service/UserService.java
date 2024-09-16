package app.cardcapture.user.service;

import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.business.domain.NewUserProductCategory;
import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import app.cardcapture.payment.business.repository.UserProductCategoryRepository;
import app.cardcapture.payment.common.service.PaymentCommonService;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.dto.UserMapper;
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

    private final PaymentCommonService paymentCommonService;
    private final UserProductCategoryRepository userProductCategoryRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User findUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(
                () -> new BusinessLogicException(ErrorCode.USER_RETRIEVAL_FAILED));
    }

    @Transactional
    public User save(User user) {
        userRepository.save(user);

        NewUserProductCategory newUserProductCategory = NewUserProductCategory.FREE_AI_POSTER_PRODUCTION_TICKET;
        paymentCommonService.saveUserProductCategory(newUserProductCategory.getProductCategory(),
            newUserProductCategory.getCount(), user);

        return user;
    }

    public boolean existsByGoogleId(String googleId) {
        return userRepository.existsByGoogleId(googleId);
    }

    public User findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId)
            .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_RETRIEVAL_FAILED));
    }

    public List<UserProductCategory> getUserProductCategories(User user) {
        return userProductCategoryRepository.findByUser(user);
    }
}