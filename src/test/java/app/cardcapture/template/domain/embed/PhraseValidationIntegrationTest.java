package app.cardcapture.template.domain.embed;

import app.cardcapture.template.domain.entity.Prompt;
import app.cardcapture.template.repository.PromptRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class PhraseValidationIntegrationTest {

    @Autowired
    private PromptRepository promptRepository;

    @Test
    public void whenPhraseIsValid_thenNoException() {
        // Given
        Phrase phrase = new Phrase(Arrays.asList("Hello", "World"), new Emphasis("1", null));
        Prompt prompt = new Prompt(null, phrase, "purpose", "Blue", "Model", null, null);

        // When & Then
        promptRepository.save(prompt);
    }

    @Test
    public void whenPhraseIsEmpty_thenConstraintViolationException() {
        // Given
        Phrase phrase = new Phrase(Arrays.asList("", ""));
        Prompt prompt = new Prompt(null, phrase, new Emphasis("1", null), "Greeting", "Blue", "Model");

        // When & Then
        assertThatThrownBy(() -> promptRepository.save(prompt))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void whenPhraseIsAllNull_thenConstraintViolationException() {
        // Given
        Phrase phrase = new Phrase(Arrays.asList(null, null));
        Prompt prompt = new Prompt(null, phrase, new Emphasis("1", null), "Greeting", "Blue", "Model");

        // When & Then
        assertThatThrownBy(() -> promptRepository.save(prompt))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void whenPhraseIsMixedInvalidAndValid_thenNoException() {
        // Given
        Phrase phrase = new Phrase(Arrays.asList("", null, "Valid Phrase"));
        Prompt prompt = new Prompt(null, phrase, new Emphasis("1", null), "Greeting", "Blue", "Model");

        // When & Then
        promptRepository.save(prompt);
    }

    @Test
    public void whenPhraseIsNullList_thenConstraintViolationException() {
        // Given
        Phrase phrase = new Phrase(null);
        Prompt prompt = new Prompt(null, phrase, new Emphasis("1", null), "Greeting", "Blue", "Model");

        // When & Then
        assertThatThrownBy(() -> promptRepository.save(prompt))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}