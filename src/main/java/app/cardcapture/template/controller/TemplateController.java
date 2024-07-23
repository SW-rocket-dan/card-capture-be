package app.cardcapture.template.controller;

import app.cardcapture.common.dto.SuccessResponseDto;
import app.cardcapture.template.dto.PromptRequestDto;
import app.cardcapture.template.dto.TemplateEditorResponseDto;
import app.cardcapture.template.dto.TemplateResponseDto;
import app.cardcapture.template.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "template", description = "The template API")
@RequestMapping("/api/v1/template")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping("/create")
    @Operation(summary = "템플릿 생성", description = "프롬프트 데이터를 받아 AI 포스터 템플릿을 생성합니다.")
    public ResponseEntity<SuccessResponseDto<TemplateEditorResponseDto>> createTemplate(
            @Valid @RequestBody PromptRequestDto promptRequestDto
    ) {
        System.out.println("promptRequestDto = " + promptRequestDto);
        TemplateEditorResponseDto templateEditorResponseDto = templateService.createTemplate(promptRequestDto);
        SuccessResponseDto<TemplateEditorResponseDto> responseDto = SuccessResponseDto.create(templateEditorResponseDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "템플릿 조회", description = "템플릿 ID를 사용하여 템플릿을 조회합니다.")
    public ResponseEntity<SuccessResponseDto<TemplateResponseDto>> getTemplate(
            @RequestParam Long id
    ) {
        TemplateResponseDto templateResponseDto = templateService.findById(id);
        SuccessResponseDto<TemplateResponseDto> responseDto = SuccessResponseDto.create(templateResponseDto);
        return ResponseEntity.ok(responseDto);
    }
}
