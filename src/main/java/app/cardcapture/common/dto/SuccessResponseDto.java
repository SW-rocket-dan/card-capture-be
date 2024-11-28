package app.cardcapture.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessResponseDto<T> {

    private final T data;

    public static <T> SuccessResponseDto<T> create(T data) {
        return new SuccessResponseDto<>(data);
    }
}
