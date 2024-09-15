package app.cardcapture.user.controller;

import app.cardcapture.common.dto.SuccessResponseDto;
import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import app.cardcapture.payment.business.dto.UserProductCategoriesResponseDto;
import app.cardcapture.security.PrincipleDetails;
import app.cardcapture.user.dto.UserMapper;
import app.cardcapture.user.dto.UserProfileResponseDto;
import app.cardcapture.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "사용자 정보 조회",
        description = "현재 로그인한 사용자의 정보를 조회합니다. JWT를 통해 사용자를 식별합니다. ")
    public ResponseEntity<SuccessResponseDto<UserProfileResponseDto>> getUserDetails(
        @AuthenticationPrincipal PrincipleDetails principle
    ) {
        UserProfileResponseDto userProfileResponseDto = userMapper.toUserProfileResponseDto(
            principle.getUser());
        SuccessResponseDto<UserProfileResponseDto> response = SuccessResponseDto.create(
            userProfileResponseDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/product-categories")
    @Operation(summary = "사용자 상품 카테고리 조회",
        description = "현재 로그인한 사용자의 상품 카테고리를 조회합니다. JWT를 통해 사용자를 식별합니다.")
    public ResponseEntity<SuccessResponseDto<UserProductCategoriesResponseDto>> getUserProductCategories(
        @AuthenticationPrincipal PrincipleDetails principle
    ) {
        List<UserProductCategory> userProductCategories = userService.getUserProductCategories(
            principle.getUser());
        UserProductCategoriesResponseDto userProductCategoriesResponseDto = UserProductCategoriesResponseDto.from(
            userProductCategories);
        SuccessResponseDto<UserProductCategoriesResponseDto> response = SuccessResponseDto.create(
            userProductCategoriesResponseDto);

        return ResponseEntity.ok(response);
    }
}
