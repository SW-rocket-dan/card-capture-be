package app.cardcapture.template.controller;

import app.cardcapture.common.dto.SuccessResponseDto;
import app.cardcapture.security.PrincipleDetails;
import app.cardcapture.template.dto.TemplateEditorResponseDto;
import app.cardcapture.template.dto.TemplateUpdateRequestDto;
import app.cardcapture.template.dto.TemplateUpdateResponseDto;
import app.cardcapture.template.dto.TemplateRequestDto;
import app.cardcapture.template.dto.TemplateResponseDto;
import app.cardcapture.template.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "template", description = "The template API")
@RequestMapping("/api/v1/template")
@RequiredArgsConstructor
@Slf4j
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping("/create")
    @Operation(summary = "템플릿 생성", description = "프롬프트 데이터를 받아 AI 포스터 템플릿을 생성합니다. 이용권이 없는 사람이 요청하면 403이 뜹니다.")
    public ResponseEntity<SuccessResponseDto<TemplateEditorResponseDto>> createTemplate(
            @Valid @RequestBody TemplateRequestDto templateRequestDto,
            @AuthenticationPrincipal PrincipleDetails principle
    ) {
        log.info("templateRequestDto = " + templateRequestDto);

        TemplateEditorResponseDto templateEditorResponseDto = templateService.createTemplate(templateRequestDto, templateRequestDto.count(), principle.getUser());
        SuccessResponseDto<TemplateEditorResponseDto> responseDto = SuccessResponseDto.create(templateEditorResponseDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "템플릿 조회", description = "템플릿 ID를 사용하여 템플릿을 조회합니다.")
    public ResponseEntity<SuccessResponseDto<TemplateResponseDto>> getTemplate(
            @PathVariable Long id
    ) {
        TemplateResponseDto templateResponseDto = templateService.findById(id);
        SuccessResponseDto<TemplateResponseDto> responseDto = SuccessResponseDto.create(templateResponseDto);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/update")
    @Operation(summary = "템플릿 수정",
            description = "템플릿 ID를 사용하여 템플릿의 원하는 요소를 수정합니다. Optional한 요소들 때문에, 변경된 요소를 같이 알려줘야 합니다." +
                    "필수: templateId,바뀐요소들 / 선택: EDITOR, TITLE, DESCRIPTION, FILE_URL" +
                    "자신이 소유한 템플릿이 아니라면 403이 뜨며 수정이 거부됩니다")
    public ResponseEntity<SuccessResponseDto<TemplateUpdateResponseDto>> updateTemplate(
            @Valid @RequestBody TemplateUpdateRequestDto templateUpdateRequestDto,
            @AuthenticationPrincipal PrincipleDetails principle
    ) {
        TemplateUpdateResponseDto templateEditorResponseDto = templateService.updateTemplateEditor(templateUpdateRequestDto, principle.getUser());
        SuccessResponseDto<TemplateUpdateResponseDto> responseDto = SuccessResponseDto.create(templateEditorResponseDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/all")
    @Operation(summary = "사용자 템플릿 조회", description = "사용자의 모든 템플릿을 조회합니다.")
    public ResponseEntity<SuccessResponseDto<List<TemplateResponseDto>>> getAllTemplatesByUser(
            @AuthenticationPrincipal PrincipleDetails principle
    ) {
        List<TemplateResponseDto> templateResponseDtos = templateService.findAllByUserId(principle.getUser().getId());
        SuccessResponseDto<List<TemplateResponseDto>> responseDto = SuccessResponseDto.create(templateResponseDtos);

        return ResponseEntity.ok(responseDto);
    }
}
