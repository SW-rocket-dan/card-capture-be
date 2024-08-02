package app.cardcapture.template.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PhraseValidatorTest {

    private ValidPhraseValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    public void setUp() {
        validator = new ValidPhraseValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    public void testValidPhrase() {
        // Given
        List<String> phrases = Arrays.asList("Hello", "World");

        // When
        boolean result = validator.isValid(phrases, context);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void testInvalidPhrase_emptyList() {
        // Given
        List<String> phrases = Arrays.asList("", "");

        // When
        boolean result = validator.isValid(phrases, context);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void testInvalidPhrase_allNull() {
        // Given
        List<String> phrases = Arrays.asList(null, null);

        // When
        boolean result = validator.isValid(phrases, context);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void testInvalidPhrase_mixedInvalidAndValid() {
        // Given
        List<String> phrases = Arrays.asList("", null, "Valid Phrase");

        // When
        boolean result = validator.isValid(phrases, context);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void testInvalidPhrase_empty() {
        // Given
        List<String> phrases = Arrays.asList();

        // When
        boolean result = validator.isValid(phrases, context);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void testInvalidPhrase_null() {
        // Given
        List<String> phrases = null;

        // When
        boolean result = validator.isValid(phrases, context);

        // Then
        assertThat(result).isFalse();
    }
}
