package app.cardcapture.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    DUPLICATED_USER(HttpStatus.CONFLICT, "User already exists"),
    USER_RETRIEVAL_FAILED(HttpStatus.NOT_FOUND, "Failed to retrieve user from the database"),
    GOOGLE_ACCESS_TOKEN_RETRIEVAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve Google access token"),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),

    REQUEST_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Request validation failed"),
    UNEXPECTED_RUNTIME_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected runtime exception"),

    NON_EXISTENT_PRODUCT_ID(HttpStatus.BAD_REQUEST, "Product ID does not exist"),
    INVALID_PRODUCT_PRICE(HttpStatus.BAD_REQUEST, "The product price is not valid"),

    MISSING_REQUIRED_LANGUAGE(HttpStatus.BAD_REQUEST, "At least one of 'english' or 'korean' must be provided"),
    JSON_PARSING_ERROR(HttpStatus.BAD_REQUEST, "Error parsing JSON response"),

    IMAGE_RETREIVAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve image from the database"),
    BACKGROUND_REMOVAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to remove background from the image"),
    IMAGE_URL_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read image from the the URL"),
    IMAGE_RAW_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to read image bytes from raw byte array"),
    MALFORMED_IMAGE_URL(HttpStatus.BAD_REQUEST, "Malformed image URL"),

    TEMPLTE_RETRIEVAL_FAILED(HttpStatus.NOT_FOUND, "Failed to retrieve template from the database"),
    TEMPLATE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have permission to access this template"),
    TEMPLATE_MODIFICATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have permission to modify this template"),

    UNMATCHED_PURCHAGE_PRODUCT_TOTAL_PRICE(HttpStatus.BAD_REQUEST, "Product total price does not match"),
    PRODUCT_VOUCHER_RETRIEVAL_FAILED(HttpStatus.FORBIDDEN, "Product voucher not found"),
    INSUFFICIENT_PRODUCT_VOUCHER(HttpStatus.FORBIDDEN, "Insufficient product voucher quantity"),
    UNMATECHED_PAYMENT_CURRENCY(HttpStatus.BAD_REQUEST, "Payment currency does not match"),
    PAYMENT_RETRIEVAL_FAILED(HttpStatus.NOT_FOUND, "Failed to retrieve payment information from the database"),
    PAYMENT_CANCELLATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to cancel the payment"),
    MONTHLY_SALES_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "Monthly sales limit exceeded"),

    PAYMENT_VERIFICATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,
        "Payment verification failed. If the payment was processed, it will be canceled. Please try again later, and if the issue persists, contact customer support."),


    USER_PRODUCT_CATEGORY_RETRIEVAL_FAILED(HttpStatus.NOT_FOUND, "Failed to retrieve user product category from the database"),
    USER_ALREADY_RECEIVED_SIGNUP_REWARD(HttpStatus.BAD_REQUEST, "User has already received the signup reward"),

    ;

    private final HttpStatus httpStatus;
    private final String message;


}
