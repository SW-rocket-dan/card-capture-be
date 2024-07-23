package app.cardcapture.template.service;

import app.cardcapture.template.dto.PhraseDto;
import app.cardcapture.template.dto.PromptRequestDto;
import app.cardcapture.template.dto.TemplateEditorResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class TemplateServiceTest {

    @InjectMocks
    private TemplateService templateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void 템플릿을_생성할_수_있다() {
        // given
        PromptRequestDto promptRequestDto = new PromptRequestDto(
                new PhraseDto(List.of("phrase1", "phrase2"), "firstEmphasis", "secondEmphasis"),
                "purpose",
                "color",
                "model"
        );

        // when
        TemplateEditorResponseDto result = templateService.createTemplate(promptRequestDto);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getTemplateId()).isEqualTo(1L),
                () -> assertThat(result.getEditor()).isEqualTo("에디터 json이 올 자리")
        );
    }
}