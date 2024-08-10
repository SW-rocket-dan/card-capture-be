package app.cardcapture.template.service;

import app.cardcapture.ai.openai.service.OpenAiFacadeService;
import app.cardcapture.ai.openai.service.OpenAiTextService;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.business.domain.ProductCategory;
import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import app.cardcapture.payment.business.repository.UserProductCategoryRepository;
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
    private final UserProductCategoryRepository userProductCategoryRepository;

    @Transactional
    public TemplateEditorResponseDto createTemplate(TemplateRequestDto templateRequestDto, User user) {
        ProductCategory productCategory = ProductCategory.AI_POSTER_PRODUCTION_TICKET;
        UserProductCategory userProductCategory = userProductCategoryRepository.findByUserAndProductCategory(user, productCategory)
                .orElseThrow(() -> new BusinessLogicException("이용권이 없습니다", HttpStatus.FORBIDDEN));
        userProductCategory.deductUsage();
        userProductCategoryRepository.save(userProductCategory);

        Prompt prompt = templateRequestDto.promptRequestDto().toEntity();
        Prompt savedPrompt = promptRepository.save(prompt);

        List<TemplateTagRequestDto> tags = templateRequestDto.templateTagRequestDtos();

        Template template = templateRequestDto.toEntity(savedPrompt);


        // template에 무언가를 열심히 설정한다
        template.setUser(user);
        String editorJson = openAiFacadeService.generateText(templateRequestDto.prompt(), user);

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
