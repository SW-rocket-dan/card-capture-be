package app.cardcapture.template.service;

import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.template.domain.entity.Prompt;
import app.cardcapture.template.domain.entity.Template;
import app.cardcapture.template.domain.entity.TemplateTag;
import app.cardcapture.template.dto.TemplateEditorResponseDto;
import app.cardcapture.template.dto.TemplateEditorUpdateRequestDto;
import app.cardcapture.template.dto.TemplateEditorUpdateResponseDto;
import app.cardcapture.template.dto.TemplateRequestDto;
import app.cardcapture.template.dto.TemplateResponseDto;
import app.cardcapture.template.dto.TemplateTagRequestDto;
import app.cardcapture.template.repository.PromptRepository;
import app.cardcapture.template.repository.TemplateRepository;
import app.cardcapture.template.repository.TemplateTagRepository;
import app.cardcapture.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TemplateService {

    private static final String USER_INFO_RETRIEVAL_ERROR = "유저 정보를 찾을 수 없습니다";
    private static final String editorJson = "{ \"cards\": [ { \"id\": 0, \"background\": { \"url\": \"\", \"opacity\": 100, \"color\": \"#FFD1DC\" }, \"layers\": [ { \"id\": 1, \"type\": \"text\", \"position\": { \"x\": 100, \"y\": 100, \"width\": 100, \"height\": 100, \"rotate\": 0, \"zIndex\": 2, \"opacity\": 100 }, \"content\": { \"content\": { \"ops\": [ { \"insert\": \"안녕하세요\" } ] } } }, { \"id\": 2, \"type\": \"text\", \"position\": { \"x\": 180, \"y\": 180, \"width\": 50, \"height\": 50, \"rotate\": 0, \"zIndex\": 1, \"opacity\": 100 }, \"content\": { \"content\": { \"ops\": [ { \"insert\": \"테스트입니다\" } ] } } }, { \"id\": 3, \"type\": \"text\", \"position\": { \"x\": 200, \"y\": 200, \"width\": 50, \"height\": 50, \"rotate\": 0, \"zIndex\": 1, \"opacity\": 100 }, \"content\": { \"content\": { \"ops\": [ { \"insert\": \"반가워요!\" } ] } } } ] } ] }";
    private final TemplateRepository templateRepository;
    private final TemplateTagRepository templateTagRepository;
    private final PromptRepository promptRepository;
    private final TemplateTagService templateTagService;

    @Transactional
    public TemplateEditorResponseDto createTemplate(TemplateRequestDto templateRequestDto, User user) {
        Prompt prompt = templateRequestDto.promptRequestDto().toEntity();
        Prompt savedPrompt = promptRepository.save(prompt);

        List<TemplateTagRequestDto> tags = templateRequestDto.templateTagRequestDtos();

        Template template = templateRequestDto.toEntity(savedPrompt);

        // template에 무언가를 열심히 설정한다
        template.setUser(user);
        template.setEditor(editorJson); //TODO: AI에서 만들어줘야 합니다

        Template savedTemplate = templateRepository.save(template);
        List<TemplateTag> savedTags = templateTagService.saveTags(tags, savedTemplate);


        return new TemplateEditorResponseDto(template.getEditor());
    }

    public TemplateResponseDto findById(Long id) {
        Template template = templateRepository.findById(id).orElseThrow(() -> new BusinessLogicException(USER_INFO_RETRIEVAL_ERROR, HttpStatus.NOT_FOUND));
        return TemplateResponseDto.fromEntity(template);
    }

    public List<TemplateResponseDto> findAllByUserId(Long userId) {
        return templateRepository.findByUserId(userId).stream()
                .map(template -> TemplateResponseDto.fromEntity(template))
                .toList();
    }

    public TemplateEditorUpdateResponseDto updateTemplateEditor(TemplateEditorUpdateRequestDto templateEditorUpdateRequestDto, User user) {
        Template template = templateRepository.findById(templateEditorUpdateRequestDto.id()).orElseThrow(()
                -> new BusinessLogicException(USER_INFO_RETRIEVAL_ERROR, HttpStatus.NOT_FOUND));
        System.out.println("user = " + user.getId());
        System.out.println("template.getUser() = " + template.getUser().getId());

        if (template.getUser().getId()!= user.getId()) {
            throw new BusinessLogicException("템플릿 수정 권한이 없습니다", HttpStatus.FORBIDDEN);
        }

        template.setEditor(templateEditorUpdateRequestDto.editor());
        templateRepository.save(template);

        return new TemplateEditorUpdateResponseDto(template.getId());
    }
}
