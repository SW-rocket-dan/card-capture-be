package app.cardcapture.payment.business.dto;

import app.cardcapture.payment.business.domain.ProductCategory;
import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record UserProductCategoriesResponseDto(
    @NotNull List<UserProductCategoryDto> userProductCategories
) {

    public static UserProductCategoriesResponseDto from(
        List<UserProductCategory> userProductCategories) {
        Map<ProductCategory, UserProductCategoryDto> userProductCategoryMap = userProductCategories.stream()
            .collect(Collectors.toMap(
                UserProductCategory::getProductCategory,
                UserProductCategoryDto::from
            ));

        List<UserProductCategoryDto> userProductCategoryDtos = new ArrayList<>();
        for (ProductCategory category : ProductCategory.values()) {
            userProductCategoryDtos.add(
                userProductCategoryMap.getOrDefault(category,
                    new UserProductCategoryDto(category, 0))
            );
        }

        return new UserProductCategoriesResponseDto(userProductCategoryDtos);
    }
}
