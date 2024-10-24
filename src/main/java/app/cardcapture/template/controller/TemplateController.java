package app.cardcapture.template.controller;

import app.cardcapture.ai.openai.dto.AiImageChangeReqeustDto;
import app.cardcapture.ai.openai.dto.AiImageChangeResponseDto;
import app.cardcapture.ai.openai.service.OpenAiFacadeService;
import app.cardcapture.common.dto.SuccessResponseDto;
import app.cardcapture.security.PrincipalDetails;
import app.cardcapture.template.dto.TemplateEditorResponseDto;
import app.cardcapture.template.dto.TemplateEmptyResponseDto;
import app.cardcapture.template.dto.TemplateSearchResponseDto;
import app.cardcapture.template.dto.TemplateUpdateRequestDto;
import app.cardcapture.template.dto.TemplateUpdateResponseDto;
import app.cardcapture.template.dto.TemplateRequestDto;
import app.cardcapture.template.dto.TemplateResponseDto;
import app.cardcapture.template.service.TemplateSearchService;
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
    private final OpenAiFacadeService openAiFacadeService;
    private final TemplateSearchService templateSearchService;

    @PostMapping("/create")
    @Operation(summary = "템플릿 생성", description = "프롬프트 데이터를 받아 AI 포스터 템플릿을 생성합니다. 이용권이 없는 사람이 요청하면 403이 뜹니다.")
    public ResponseEntity<SuccessResponseDto<TemplateEditorResponseDto>> createTemplate(
        @Valid @RequestBody TemplateRequestDto templateRequestDto,
        @AuthenticationPrincipal PrincipalDetails principle
    ) {
        log.info("templateRequestDto = " + templateRequestDto);

        TemplateEditorResponseDto templateEditorResponseDto = templateService.createTemplate(
            templateRequestDto, principle.getUser());
        SuccessResponseDto<TemplateEditorResponseDto> responseDto = SuccessResponseDto.create(
            templateEditorResponseDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/create-empty")
    @Operation(summary = "빈 템플릿 생성", description = "빈 템플릿을 생성합니다. 이용권이 없어도 생성할 수 있습니다.")
    public ResponseEntity<SuccessResponseDto<TemplateEmptyResponseDto>> createEmptyTemplate(
        @AuthenticationPrincipal PrincipalDetails principle
    ) {
        TemplateEmptyResponseDto templateEmptyResponseDto = templateService.createEmptyTemplate(principle.getUser());
        SuccessResponseDto<TemplateEmptyResponseDto> responseDto = SuccessResponseDto.create(
            templateEmptyResponseDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "템플릿 조회", description = "템플릿 ID를 사용하여 템플릿을 조회합니다.")
    public ResponseEntity<SuccessResponseDto<TemplateResponseDto>> getTemplate(
        @PathVariable Long id,
        @AuthenticationPrincipal PrincipalDetails principle
    ) {
        TemplateResponseDto templateResponseDto = templateService.findById(id, principle.getUser());
        SuccessResponseDto<TemplateResponseDto> responseDto = SuccessResponseDto.create(
            templateResponseDto);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/update")
    @Operation(summary = "템플릿 수정",
        description = "템플릿 ID를 사용하여 템플릿의 원하는 요소를 수정합니다. Optional한 요소들 때문에, 변경된 요소를 같이 알려줘야 합니다." +
            "필수: templateId,바뀐요소들 / 선택: EDITOR, TITLE, DESCRIPTION, FILE_URL" +
            "자신이 소유한 템플릿이 아니라면 403이 뜨며 수정이 거부됩니다")
    public ResponseEntity<SuccessResponseDto<TemplateUpdateResponseDto>> updateTemplate(
        @Valid @RequestBody TemplateUpdateRequestDto templateUpdateRequestDto,
        @AuthenticationPrincipal PrincipalDetails principle
    ) {
        TemplateUpdateResponseDto templateEditorResponseDto = templateService.updateTemplateEditor(
            templateUpdateRequestDto, principle.getUser());
        SuccessResponseDto<TemplateUpdateResponseDto> responseDto = SuccessResponseDto.create(
            templateEditorResponseDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/all")
    @Operation(summary = "사용자 템플릿 조회", description = "사용자의 모든 템플릿을 조회합니다.")
    public ResponseEntity<SuccessResponseDto<List<TemplateResponseDto>>> getAllTemplatesByUser(
        @AuthenticationPrincipal PrincipalDetails principle
    ) {
        List<TemplateResponseDto> templateResponseDtos = templateService.findAllByUserId(
            principle.getUser().getId());
        SuccessResponseDto<List<TemplateResponseDto>> responseDto = SuccessResponseDto.create(
            templateResponseDtos);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/change-image")
    @Operation(summary = "이미지 변경",
        description = "템플릿의 이미지 하나를 변경합니다. "
            + "aiImage의 고유 id와, 변경하고 싶은 message를 보내주면 됩니다. 기존 이미지 느낌에서 message를 더하는 느낌으로 변경됩니다."
                + "배경, 스티커, 메인이미지 등 ai로 생성한 이미지라면 모두 변경 가능합니다. "
            + "단, 현재는 KST 8/18 이후에 생성된 이미지만 변경 가능합니다. 추후 모든 AI 이미지뿐만 아니라 사용자가 업로드한 이미지도 변경 가능하게 할 예정입니다."
            + "현재는 배경 제거가 되지 않은 이미지만 생성됩니다. 추후 배경 제거된 이미지도 같이 받을지 선택할 수 있게 할 예정입니다.")
    public ResponseEntity<SuccessResponseDto<AiImageChangeResponseDto>> getChangedAiImage(
        @Valid @RequestBody AiImageChangeReqeustDto aiImageChangeReqeustDto,
        @AuthenticationPrincipal PrincipalDetails principle
    ) {
        AiImageChangeResponseDto aiImageChangeResponseDto = openAiFacadeService.changeAiImage(
            aiImageChangeReqeustDto, principle.getUser()); //TODO: 배경 제거 선택 기능 넣기
        SuccessResponseDto<AiImageChangeResponseDto> responseDto = SuccessResponseDto.create(
            aiImageChangeResponseDto);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/search/{query}")
    @Operation(summary = "템플릿 검색", description = "템플릿을 특정 단어 1개로 검색합니다.")
    public ResponseEntity<SuccessResponseDto<TemplateSearchResponseDto>> searchTemplate(
        @PathVariable String query
    ) {
        TemplateSearchResponseDto templateResponseDtos = templateSearchService.searchByTitleField(query);
        SuccessResponseDto<TemplateSearchResponseDto> responseDto = SuccessResponseDto.create(
            templateResponseDtos);

        return ResponseEntity.ok(responseDto);
    }
}
