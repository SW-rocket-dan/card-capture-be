package app.cardcapture.template.service;

import app.cardcapture.template.dto.PromptRequestDto;
import app.cardcapture.template.dto.TemplateEditorResponseDto;
import app.cardcapture.template.dto.TemplateResponseDto;
import app.cardcapture.template.repository.TemplateRepository;
import app.cardcapture.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateEditorResponseDto createTemplate(PromptRequestDto promptRequestDto) {
        // Template template = new Template();
        // template에 무언가를 열심히 설정한다
        // templateRepository.save(template);
        return new TemplateEditorResponseDto(1L, "에디터 json이 올 자리");
    }

    public TemplateResponseDto findById(Long id) {
        // return templateRepository.findById(id).orElseThrow(() -> new BusinessLogicException(USER_INFO_RETRIEVAL_ERROR, HttpStatus.NOT_FOUND));
        return new TemplateResponseDto(
                1L,
                new User(),
                "title",
                "description",
                0,
                0,
                "editor",
                "fileUrl",
                null, null, null, null);
    }
}
