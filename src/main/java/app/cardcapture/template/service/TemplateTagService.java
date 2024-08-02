package app.cardcapture.template.service;

import app.cardcapture.sticker.domain.entity.Sticker;
import app.cardcapture.sticker.domain.entity.StickerTag;
import app.cardcapture.sticker.dto.TagDto;
import app.cardcapture.template.domain.entity.Template;
import app.cardcapture.template.domain.entity.TemplateTag;
import app.cardcapture.template.dto.TemplateTagRequestDto;
import app.cardcapture.template.repository.TemplateTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateTagService { //TODO: 겹치는 Tag에 대해 추상화 고려

    private final TemplateTagRepository templateTagRepository;

    public List<TemplateTag> saveTags(List<TemplateTagRequestDto> tagDtos, Template savedTemplate) {
        return tagDtos.stream()
                .map(tagRequest -> {
                    TemplateTag templateTag = new TemplateTag();
                    templateTag.setTemplate(savedTemplate);
                    templateTag.setKorean(tagRequest.korean());
                    templateTag.setEnglish(tagRequest.english());
                    return templateTagRepository.save(templateTag);
                })
                .toList();
    }
}
