package app.cardcapture.template.service;

import app.cardcapture.ai.openai.service.OpenAiFacadeService;
import app.cardcapture.ai.openai.service.OpenAiTextService;
import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.payment.business.domain.ProductCategory;
import app.cardcapture.payment.business.domain.entity.UserProductCategory;
import app.cardcapture.payment.business.repository.UserProductCategoryRepository;
import app.cardcapture.template.domain.TemplateAttribute;
import app.cardcapture.template.domain.entity.Prompt;
import app.cardcapture.template.domain.entity.Template;
import app.cardcapture.template.dto.TemplateEditorResponseDto;
import app.cardcapture.template.dto.TemplateEmptyResponseDto;
import app.cardcapture.template.dto.TemplateUpdateRequestDto;
import app.cardcapture.template.dto.TemplateUpdateResponseDto;
import app.cardcapture.template.dto.TemplateRequestDto;
import app.cardcapture.template.dto.TemplateResponseDto;
import app.cardcapture.template.repository.PromptRepository;
import app.cardcapture.template.repository.TemplateRepository;
import app.cardcapture.template.repository.TemplateTagRepository;
import app.cardcapture.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateTagRepository templateTagRepository;
    private final PromptRepository promptRepository;
    private final TemplateTagService templateTagService;
    private final OpenAiTextService openAiTextService;
    private final UserProductCategoryRepository userProductCategoryRepository;
    private final OpenAiFacadeService openAiFacadeService;

    @Transactional // TODO: 템플릿 생성 버튼 눌렀을 때, 사용자가 마이페이지에서 확인할 수 있게 프론트랑 뭐 같이 해야할 듯
    public TemplateEditorResponseDto createTemplate(TemplateRequestDto templateRequestDto,
        User user) {
        ProductCategory productCategory = ProductCategory.AI_POSTER_PRODUCTION_TICKET;

        UserProductCategory userProductCategory = userProductCategoryRepository.findByUserAndProductCategoryWithLock(
                user, productCategory)
            .orElseThrow(() -> new BusinessLogicException(ErrorCode.PRODUCT_VOUCHER_RETRIEVAL_FAILED));

        // 사용 가능한 이용권이 있는지 확인
//        if (userProductCategory.getQuantity() < 1) {
//            throw new BusinessLogicException(ErrorCode.INSUFFICIENT_PRODUCT_VOUCHER);
//        } // TODO: 따닥 문제는 클라이언트에서도 막고 1초 이내에 요청 1개만 받기로 해결
        // TODO: 확신이 없는 부분은 테스트 해봐도 괜찮을듯

//        userProductCategory.deductUsage(); // TODO: deduct 쿼리를 직접 짜기. -1 하되 0보다 클 때만. 0보다 큰 경 그 떄 잡아서 BusinessLogicException 던지기
        // TODO:update user_product_categories set quantity=quantity-1 where user_id=2 and quantity>0;
        userProductCategoryRepository.save(userProductCategory);

        Prompt prompt = templateRequestDto.prompt().toEntity();
        Prompt savedPrompt = promptRepository.save(prompt);

        // TODO: tag 관련 기획 확정되면 template create dto 설계 다시해야할듯
        // List<TemplateTagRequestDto> tags = templateRequestDto.templateTags();

        Template template = templateRequestDto.toEntity(savedPrompt);

        // template에 무언가를 열심히 설정한다
        template.setUser(user);
        String editorJson = openAiFacadeService.generateText(templateRequestDto.prompt(),
            templateRequestDto.count(), user);

        template.setEditor(editorJson);
        template.setTitle(templateRequestDto.prompt().phraseDetails().phrases().get(0));
        template.setDescription(templateRequestDto.prompt().purpose());
        template.setFileUrl(
            "https://cardcaptureposterimage.s3.ap-northeast-2.amazonaws.com/default/incompleteAnnouncement.png"); //TODO: 잘못됐어 대기열넣고 뜯어고쳐야함

        Template savedTemplate = templateRepository.save(template);
        // List<TemplateTag> savedTags = templateTagService.saveTags(tags, savedTemplate);

        return new TemplateEditorResponseDto(savedTemplate.getId(), savedTemplate.getEditor());
    }

    public TemplateResponseDto findById(Long id, User user) {
        Template template = templateRepository.findById(id).orElseThrow(
            () -> new BusinessLogicException(ErrorCode.TEMPLTE_RETRIEVAL_FAILED));

        if (template.getUser().getId()!=user.getId()) {
            throw new BusinessLogicException(ErrorCode.TEMPLATE_ACCESS_DENIED);
        }

        return TemplateResponseDto.fromEntity(template);
    }

    public List<TemplateResponseDto> findAllByUserId(Long userId) {
        return templateRepository.findByUserId(userId).stream()
            .map(template -> TemplateResponseDto.fromEntity(template))
            .toList();
    }

    public TemplateUpdateResponseDto updateTemplateEditor(
        TemplateUpdateRequestDto templateUpdateRequestDto, User user) {
        Template template = templateRepository.findById(templateUpdateRequestDto.id())
            .orElseThrow(()
                -> new BusinessLogicException(ErrorCode.USER_RETRIEVAL_FAILED));

        if (template.getUser().getId() != user.getId()) {
            throw new BusinessLogicException(ErrorCode.TEMPLATE_MODIFICATION_ACCESS_DENIED);
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

    public TemplateEmptyResponseDto createEmptyTemplate(User user) {
        Template template = new Template();
        template.setEditor(String.format("""
            [{ "id": %s, "background": { "url": "", "opacity": 100, "color": "#FFFFFF"}, "layers": [] }]""".replace("\n", ""),user.getId()));
        template.setTitle("제목이 없습니다.");
        template.setDescription("설명이 없습니다.");
        template.setFileUrl(
            "https://cardcaptureposterimage.s3.ap-northeast-2.amazonaws.com/default/incompleteAnnouncement.png");
        template.setUser(user);

        return TemplateEmptyResponseDto.fromEntity(templateRepository.save(template));
    }
}
