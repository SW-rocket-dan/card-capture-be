package app.cardcapture.sticker.controller;

import app.cardcapture.common.dto.SuccessResponseDto;
import app.cardcapture.sticker.dto.StickerResponseDto;
import app.cardcapture.sticker.dto.StickerSaveRequestDto;
import app.cardcapture.sticker.service.StickerService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "sticker", description = "The sticker API")
@RequestMapping("/api/v1/sticker")
@RequiredArgsConstructor
public class StickerController {
    private final StickerService stickerService;

    @Hidden
    @PostMapping("/save")
    public ResponseEntity<StickerResponseDto> saveSticker(
            @RequestBody StickerSaveRequestDto stickerSaveRequestDto
    ) {
        StickerResponseDto stickerResponseDto = stickerService.saveStickerWithTags(
                stickerSaveRequestDto.getFileUrl(),
                stickerSaveRequestDto.getTags());
        return ResponseEntity.ok(stickerResponseDto);
    }

    @Operation(summary = "검색어를 통해 스티커 조회",
            description = "검색어로 스티커를 조회합니다. 한글 또는 영어 태그 중 하나라도 검색어가 포함되면 해당 스티커를 반환합니다. 영어는 대소문자 상관없이 검색됩니다. " +
                    "검색 가능한 태그는 다음과 같습니다.(영문) 이는 예시이며, 스웨거에 없는 다른 것들도 스티커가 있다면 검색될 수 있습니다." +
                    "Apple, Banana, Cherry, Date, Elderberry, Fig, Grape, Honeydew, Ivy Gourd, Jackfruit, Kiwi, Lemon, Mango, Nectarine, Orange, Papaya, Quince, Raspberry, Strawberry, Tomato, Ugli Fruit," +
                    "Cat, Dog, Flower, Heart, Star, Cake, Balloon, Gift, Rainbow, Sunshine, Moon, Cloud, Car, Airplane, Boat, Train, Mountain, Tree, Beach, Sea, Book, Instrument, Music, Movie, Sports, Travel, Camera, Smile, Happiness, Friendship, Love, Fashion, Art, Food, Coffee, Tea, Dessert, Celebration, Party, Birthday, Wedding, Shoes, Clothing, Accessory, Family, Nature, Animal, Plant"
    )
    @GetMapping("/search")
    public ResponseEntity<SuccessResponseDto<List<StickerResponseDto>>> searchStickers(
            @RequestParam String searchTerm
    ) {
        List<StickerResponseDto> stickers = stickerService.searchStickers(searchTerm);
        SuccessResponseDto<List<StickerResponseDto>> response = SuccessResponseDto.create(stickers);

        return ResponseEntity.ok(response);
    }
}
