package app.cardcapture.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DUPLICATED_USER(HttpStatus.CONFLICT, "User already exists"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Server error"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Not found"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    REQUEST_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Request validation failed"),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "Data integrity violation"),
    NON_EXISTENT_PRODUCT_ID(HttpStatus.BAD_REQUEST, "Product ID does not exist"),
    INVALID_PRODUCT_PRICE(HttpStatus.BAD_REQUEST, "The product price is not valid"),
    MISSING_REQUIRED_LANGUAGE(HttpStatus.BAD_REQUEST,
        "At least one of 'english' or 'korean' must be provided"),
    JSON_PARSING_ERROR(HttpStatus.BAD_REQUEST, "Error parsing JSON response"),
    MALFORMED_IMAGE_URL(HttpStatus.BAD_REQUEST, "Malformed image URL"),
    TEMPLATE_ACCESS_DENIED(HttpStatus.FORBIDDEN,
        "You do not have permission to access this template"),
    TEMPLATE_MODIFICATION_ACCESS_DENIED(HttpStatus.FORBIDDEN,
        "You do not have permission to modify this template"),
    UNMATCHED_PURCHAGE_PRODUCT_TOTAL_PRICE(HttpStatus.BAD_REQUEST,
        "Product total price does not match"),
    PRODUCT_VOUCHER_RETRIEVAL_FAILED(HttpStatus.FORBIDDEN, "Product voucher not found"),
    INSUFFICIENT_PRODUCT_VOUCHER(HttpStatus.FORBIDDEN, "Insufficient product voucher quantity"),
    UNMATECHED_PAYMENT_CURRENCY(HttpStatus.BAD_REQUEST, "Payment currency does not match"),
    MONTHLY_SALES_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "Monthly sales limit exceeded"),
    USER_ALREADY_RECEIVED_SIGNUP_REWARD(HttpStatus.BAD_REQUEST,
        "User has already received the signup reward"),
    TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "Request timeout"),
    ;

    private final HttpStatus httpStatus;
    private final String message;


}
