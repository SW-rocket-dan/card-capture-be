package app.cardcapture.sticker.service;

import app.cardcapture.sticker.domain.Sticker;
import app.cardcapture.sticker.domain.Tag;
import app.cardcapture.sticker.dto.TagDto;
import app.cardcapture.sticker.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> saveTags(List<TagDto> tagDtos, Sticker savedSticker) {
        return tagDtos.stream()
                .map(tagRequest -> {
                    Tag tag = new Tag();
                    tag.setSticker(savedSticker);
                    tag.setKorean(tagRequest.getKorean());
                    tag.setEnglish(tagRequest.getEnglish());
                    return tagRepository.save(tag);
                })
                .toList();
    }
}
