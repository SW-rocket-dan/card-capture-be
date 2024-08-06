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
    public static UserProductCategoriesResponseDto from(List<UserProductCategory> userProductCategories) { // TODO: Service에서 하는 건 문제없음, 다만 어차피 클라이언트가 ProductCategory를 알고 있으면 안내려주면 0이라고 판단해도 상관없음
        // Map<ProductCategory, UserProductCategoryDto>로 변환
        Map<ProductCategory, UserProductCategoryDto> userProductCategoryMap = userProductCategories.stream() // TODO: 코드거 너무 더럽긴한데... 추후에 책임 분리해서 리팩터링하자
                .collect(Collectors.toMap(
                        UserProductCategory::getProductCategory,
                        UserProductCategoryDto::from
                ));

        // 모든 ProductCategory에 대해 UserProductCategoryDto 생성
        List<UserProductCategoryDto> userProductCategoryDtos = new ArrayList<>();
        for (ProductCategory category : ProductCategory.values()) {
            userProductCategoryDtos.add(
                    userProductCategoryMap.getOrDefault(category, new UserProductCategoryDto(category, 0))
            );
        }

        return new UserProductCategoriesResponseDto(userProductCategoryDtos);
    }
}
