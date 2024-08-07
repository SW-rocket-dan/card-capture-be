package app.cardcapture.template.service;

import app.cardcapture.ai.openai.service.OpenAiTextService;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.template.domain.TemplateAttribute;
import app.cardcapture.template.domain.entity.Prompt;
import app.cardcapture.template.domain.entity.Template;
import app.cardcapture.template.domain.entity.TemplateTag;
import app.cardcapture.template.dto.TemplateEditorResponseDto;
import app.cardcapture.template.dto.TemplateUpdateRequestDto;
import app.cardcapture.template.dto.TemplateUpdateResponseDto;
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
import java.util.Set;


@Service
@RequiredArgsConstructor
public class TemplateService {

    private static final String USER_INFO_RETRIEVAL_ERROR = "유저 정보를 찾을 수 없습니다";
    private final TemplateRepository templateRepository;
    private final TemplateTagRepository templateTagRepository;
    private final PromptRepository promptRepository;
    private final TemplateTagService templateTagService;
    private final OpenAiTextService openAiTextService;

    @Transactional
    public TemplateEditorResponseDto createTemplate(TemplateRequestDto templateRequestDto, User user) {
        if (templateRepository.findAllWithLock().size()>50) {
            throw new BusinessLogicException("현재 템플릿은 50개까지만 생성할 수 있습니다", HttpStatus.BAD_REQUEST);
        }

        // TODO: 횟수가 없으면 아예 하면 안된다 (뭐 기획에 따라 생성 전까지만 하든지)

        Prompt prompt = templateRequestDto.promptRequestDto().toEntity();
        Prompt savedPrompt = promptRepository.save(prompt);

        List<TemplateTagRequestDto> tags = templateRequestDto.templateTagRequestDtos();

        Template template = templateRequestDto.toEntity(savedPrompt);

        // TODO: 사용자의 횟수를 한 번 차감시켜야한다

        // template에 무언가를 열심히 설정한다
        template.setUser(user);
        String editorJson = openAiTextService.generateText(templateRequestDto.promptRequestDto());

        template.setEditor(editorJson);




        Template savedTemplate = templateRepository.save(template);
        List<TemplateTag> savedTags = templateTagService.saveTags(tags, savedTemplate);

        return new TemplateEditorResponseDto(savedTemplate.getId(), savedTemplate.getEditor());
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

    public TemplateUpdateResponseDto updateTemplateEditor(TemplateUpdateRequestDto templateUpdateRequestDto, User user) {
        Template template = templateRepository.findById(templateUpdateRequestDto.id()).orElseThrow(()
                -> new BusinessLogicException(USER_INFO_RETRIEVAL_ERROR, HttpStatus.NOT_FOUND));

        if (template.getUser().getId()!= user.getId()) {
            throw new BusinessLogicException("템플릿 수정 권한이 없습니다", HttpStatus.FORBIDDEN);
        }

        Set<TemplateAttribute> updatedAttributes = templateUpdateRequestDto.updatedAttributes();

        if (updatedAttributes.contains(TemplateAttribute.EDITOR)) {
            template.setEditor(templateUpdateRequestDto.editor());
        } // TODO: 메서드 분리

        if (updatedAttributes.contains(TemplateAttribute.TITLE)) {
            template.setTitle(templateUpdateRequestDto.title());
        }

        if (updatedAttributes.contains(TemplateAttribute.DESCRIPTION)) {
            template.setDescription(templateUpdateRequestDto.description());
        }

        if (updatedAttributes.contains(TemplateAttribute.FILE_URL)) {
            template.setFileUrl(templateUpdateRequestDto.fileUrl());
        }
        templateRepository.save(template);

        return new TemplateUpdateResponseDto(template.getId());
    }
}
