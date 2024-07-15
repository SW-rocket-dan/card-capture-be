package app.cardcapture.sticker.controller;

import app.cardcapture.sticker.dto.StickerResponseDto;
import app.cardcapture.sticker.dto.StickerSaveRequestDto;
import app.cardcapture.sticker.dto.TagDto;
import app.cardcapture.sticker.service.StickerService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "sticker", description = "The sticker API")
@RequestMapping("/sticker")
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
            description = "검색어로 스티커를 조회합니다. 한글 또는 영어 태그 중 하나라도 일치하면 해당 스티커를 반환합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<StickerResponseDto>> searchStickers(
            @RequestParam String searchTerm
    ) {
        List<StickerResponseDto> stickers = stickerService.searchStickers(searchTerm);
        return ResponseEntity.ok(stickers);
    }

}
