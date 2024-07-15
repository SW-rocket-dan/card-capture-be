package app.cardcapture.common.dto;

import lombok.Builder;
import lombok.Getter;

//https://velog.io/@kimdy0915/Spring-API-%EC%9A%94%EC%B2%AD-%EC%84%B1%EA%B3%B5-%EC%98%88%EC%99%B8%EC%B2%98%EB%A6%AC%EC%97%90%EC%84%9C-%EA%B3%B5%ED%86%B5%EC%9C%BC%EB%A1%9C-%EC%82%AC%EC%9A%A9%ED%95%98%EB%8A%94-ResponseDto-%EA%B5%AC%ED%98%84
@Getter
public class ApiResponseDto<T> {

    private boolean success;
    private T response;
    private ErrorResponse error;

    @Builder
    private ApiResponseDto(boolean success, T response, ErrorResponse error) {
        this.success = success;
        this.response = response;
        this.error = error;
    }

}