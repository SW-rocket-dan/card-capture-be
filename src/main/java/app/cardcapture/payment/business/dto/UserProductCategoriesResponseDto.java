package app.cardcapture.payment.business.dto;

import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UserProductCategoriesResponseDto(
        @NotNull List<UserProductCategoryDto> userProductCategories
) {
    public static UserProductCategoriesResponseDto from(List<UserProductCategory> userProductCategories) {
        return new UserProductCategoriesResponseDto(
                userProductCategories.stream()
                        .map(UserProductCategoryDto::from)
                        .toList()
        );
    }
}
