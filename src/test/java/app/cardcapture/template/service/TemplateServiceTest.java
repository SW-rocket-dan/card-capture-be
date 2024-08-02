package app.cardcapture.template.service;

import app.cardcapture.template.dto.TemplateEditorResponseDto;
import app.cardcapture.template.dto.TemplateResponseDto;
import app.cardcapture.user.domain.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class TemplateServiceTest {
/*
    @InjectMocks
    private TemplateService templateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void 템플릿을_생성할_수_있다() {
        // given
        TemplateResponseDto templateResponseDto = new TemplateResponseDto(
                1L,
                "title",
                "description",
                0,
                0,
                "editor",
                "fileUrl",
                List.of(),
                null,
                null,
                null
        );
        ObjectMapper objectMapper = new ObjectMapper();

        // when
        TemplateEditorResponseDto result = templateService.createTemplate(templateResponseDto, new User());
        String editorJson = result.getEditor();

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getTemplateId()).isEqualTo(1L),
                () -> Assertions.assertThatCode(() -> objectMapper.readTree(editorJson))
                        .doesNotThrowAnyException()

        );
    }*/
}