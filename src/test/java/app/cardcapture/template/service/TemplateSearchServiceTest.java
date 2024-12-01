package app.cardcapture.template.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.template.domain.TemplateAttribute;
import app.cardcapture.template.domain.entity.Template;
import app.cardcapture.template.dto.TemplateUpdateRequestDto;
import app.cardcapture.template.repository.TemplateRepository;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.UpdateRequest;
import org.opensearch.client.opensearch.core.UpdateResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class TemplateSearchServiceTest {

    @Mock
    private OpenSearchClient openSearchClient;

    @Mock
    private TemplateRepository templateRepository;

    @InjectMocks
    private TemplateSearchService templateSearchService;

    private Template template;
    private TemplateUpdateRequestDto templateUpdateRequestDto;

    @BeforeEach
    public void setUp() {
        template = new Template();
        template.setId(1L);
        template.setTitle("Old Title");

        templateUpdateRequestDto = new TemplateUpdateRequestDto(
            1L,
            "newEditor",
            "New Title",
            "New Description",
            "new-file-url.jpg",
            List.of(),
            Set.of(TemplateAttribute.TITLE, TemplateAttribute.DESCRIPTION),
            LocalDateTime.now()
        );
    }

    @Test
    public void 템플릿_업데이트_성공() throws IOException {
        // given
        given(templateRepository.findById(templateUpdateRequestDto.id())).willReturn(Optional.of(template));
        UpdateResponse<JsonNode> mockResponse = mock(UpdateResponse.class);
        given(openSearchClient.update(any(UpdateRequest.class), eq(JsonNode.class))).willReturn(mockResponse);

        // when
        templateSearchService.update(templateUpdateRequestDto);

        // then
        verify(templateRepository, times(1)).findById(templateUpdateRequestDto.id());
        verify(openSearchClient, times(1)).update(any(UpdateRequest.class), eq(JsonNode.class));
    }

    @Test
    public void 템플릿_업데이트_실패_템플릿_찾을_수_없음() throws IOException {
        // given
        given(templateRepository.findById(templateUpdateRequestDto.id())).willReturn(Optional.empty());

        // when & then
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            templateSearchService.update(templateUpdateRequestDto);
        });

        assertEquals(ErrorCode.NOT_FOUND, exception.getErrorCode());
        verify(openSearchClient, never()).update(any(UpdateRequest.class), eq(JsonNode.class));
    }

    @Test
    public void 템플릿_업데이트_실패_OpenSearch_예외() throws IOException {
        // given
        when(templateRepository.findById(templateUpdateRequestDto.id())).thenReturn(Optional.of(template));
        when(openSearchClient.update(any(UpdateRequest.class), eq(JsonNode.class))).thenThrow(IOException.class);

        // when & then
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            templateSearchService.update(templateUpdateRequestDto);
        });

        assertEquals(ErrorCode.SERVER_ERROR, exception.getErrorCode());
        verify(templateRepository, times(1)).findById(templateUpdateRequestDto.id());
        verify(openSearchClient, times(1)).update(any(UpdateRequest.class), eq(JsonNode.class));
    }
}