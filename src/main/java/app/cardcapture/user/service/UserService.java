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
import app.cardcapture.user.dto.UserGoogleAuthResponseDto;
import app.cardcapture.user.repository.UserRepository;
import app.cardcapture.user.repository.UserRoleRepository;
import java.time.LocalDateTime;
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
                () -> new BusinessLogicException(ErrorCode.NOT_FOUND));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User save(User user) {

        saveUniqueUser(user);
        assignAndSaveSignupReward(user);
        assignAndSaveUserRole(user);

        return user;
    }

    public boolean existsByGoogleId(String googleId) {
        return userRepository.existsByGoogleId(googleId);
    }

    public User findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId)
            .orElseThrow(() -> new BusinessLogicException(ErrorCode.NOT_FOUND));
    }

    public List<UserProductCategory> getUserProductCategories(User user) {
        return userProductCategoryRepository.findByUser(user);
    }

    public User findOrCreateUser(String googleId,
        UserGoogleAuthResponseDto userGoogleAuthResponseDto) {
        return userRepository.findByGoogleId(googleId)
            .map(existingUser -> updateUser(existingUser, userGoogleAuthResponseDto))
            .orElseGet(() -> createUser(googleId, userGoogleAuthResponseDto));
    }

    private void saveUniqueUser(User user) {
        userRepository.findByGoogleId(user.getGoogleId())
            .ifPresentOrElse(exists -> {
                    throw new BusinessLogicException(ErrorCode.DUPLICATED_USER);
                },
                () -> {
                    userRepository.save(user); // TODO: 또 에러 발생할 수 있음 수정하기  / 테스트코드에서는 service단에서 동시성 검사할 필요 없이 예외 잘 뜨는지 확인정도하고, repositoryTest에서 unique constraint 잘 걸려있는지 테스트 / 그리고 굳이 excutorservice로 동시성확인까지 안해도 순차적으로 service 호출해도 테스트 가능
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

    private User createUser(String googleId, UserGoogleAuthResponseDto dto) {
        User newUser = new User();

        newUser.setGoogleId(googleId);
        newUser.setEmail(dto.email());
        newUser.setName(dto.name());
        newUser.setGivenName(dto.givenName());
        newUser.setFamilyName(dto.familyName());
        newUser.setPicture(dto.picture());
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        return newUser;
    }

    private User updateUser(User user, UserGoogleAuthResponseDto dto) {
        user.setEmail(dto.email());
        user.setName(dto.name());
        user.setGivenName(dto.givenName());
        user.setFamilyName(dto.familyName());
        user.setPicture(dto.picture());
        user.setUpdatedAt(LocalDateTime.now());

        return user;
    }
}