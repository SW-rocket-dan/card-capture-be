package app.cardcapture.template.service;

import app.cardcapture.template.dto.PromptRequestDto;
import app.cardcapture.template.dto.TemplateEditorResponseDto;
import app.cardcapture.template.dto.TemplateResponseDto;
import app.cardcapture.template.repository.TemplateRepository;
import app.cardcapture.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private static final String editorJson = "{ \"cards\": [ { \"id\": 0, \"background\": { \"url\": \"\", \"opacity\": 100, \"color\": \"#FFD1DC\" }, \"layers\": [ { \"id\": 1, \"type\": \"text\", \"position\": { \"x\": 100, \"y\": 100, \"width\": 100, \"height\": 100, \"rotate\": 0, \"zIndex\": 2, \"opacity\": 100 }, \"content\": { \"content\": { \"ops\": [ { \"insert\": \"안녕하세요\" } ] } } }, { \"id\": 2, \"type\": \"text\", \"position\": { \"x\": 180, \"y\": 180, \"width\": 50, \"height\": 50, \"rotate\": 0, \"zIndex\": 1, \"opacity\": 100 }, \"content\": { \"content\": { \"ops\": [ { \"insert\": \"테스트입니다\" } ] } } }, { \"id\": 3, \"type\": \"text\", \"position\": { \"x\": 200, \"y\": 200, \"width\": 50, \"height\": 50, \"rotate\": 0, \"zIndex\": 1, \"opacity\": 100 }, \"content\": { \"content\": { \"ops\": [ { \"insert\": \"반가워요!\" } ] } } } ] } ] }";
    private final TemplateRepository templateRepository;

    public TemplateEditorResponseDto createTemplate(PromptRequestDto promptRequestDto) {
        // Template template = new Template();
        // template에 무언가를 열심히 설정한다
        // templateRepository.save(template);
        return new TemplateEditorResponseDto(1L, editorJson);
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
