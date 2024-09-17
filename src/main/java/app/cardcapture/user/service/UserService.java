package app.cardcapture.user.service;

import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.business.domain.NewUserProductCategory;
import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import app.cardcapture.payment.business.repository.UserProductCategoryRepository;
import app.cardcapture.payment.common.service.PaymentCommonService;
import app.cardcapture.user.domain.Role;
import app.cardcapture.user.domain.entity.User;
import app.cardcapture.user.domain.entity.UserRole;
import app.cardcapture.user.repository.UserRepository;
import app.cardcapture.user.repository.UserRoleRepository;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final PaymentCommonService paymentCommonService;
    private final UserProductCategoryRepository userProductCategoryRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public User findUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(
                () -> new BusinessLogicException(ErrorCode.USER_RETRIEVAL_FAILED));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User save(User user) {

        saveUniqueUser(user);
        assignAndSaveSignupReward(user);
        assignAndSaveUserRole(user);

        return user;
    }

    private void saveUniqueUser(User user) {
        userRepository.findByGoogleId(user.getGoogleId())
            .ifPresentOrElse(exists -> {
                    throw new BusinessLogicException(ErrorCode.DUPLICATED_USER);
                },
                () -> {
                    userRepository.save(user);
                });
    }

    private void assignAndSaveUserRole(User user) {
        userRoleRepository.findByUserAndRole(user, Role.USER)
            .ifPresentOrElse(
                userRole -> {
                    throw new BusinessLogicException(ErrorCode.DUPLICATED_USER);
                },
                () -> {
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(Role.USER);
                    user.setRoles(Set.of(userRole));
                    userRoleRepository.save(userRole);
                }
            );
    }

    private void assignAndSaveSignupReward(User user) {
        NewUserProductCategory newUserProductCategory = NewUserProductCategory.FREE_AI_POSTER_PRODUCTION_TICKET;
        paymentCommonService.saveUserProductCategory(newUserProductCategory.getProductCategory(),
            newUserProductCategory.getCount(), user);
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