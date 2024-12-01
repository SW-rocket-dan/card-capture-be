package app.cardcapture.s3.controller;

import app.cardcapture.common.dto.SuccessResponseDto;
import app.cardcapture.s3.dto.PresignedUrlResponseDto;
import app.cardcapture.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/s3")
@Tag(name = "S3 Controller", description = "S3 파일 업로드, 삭제, 업데이트 및 조회 API")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/generate-presigned-url")
    @Operation(
        summary = "프리사인 URL 발급",
        description = "S3에 파일을 업로드할 수 있는 프리사인 URL과, 성공 시 사용할 이미지 URL을 반환합니다." +
            " 응답된 URL에 {key:file value:업로드할 파일}을 'binary'로 body로 담아서 HTTP PUT으로 보내면 저장이 됩니다. " +
            "png같은 확장자를 정해줘야 합니다."
    )
    public ResponseEntity<SuccessResponseDto<PresignedUrlResponseDto>> generatePresignedUrl(
        @Parameter(description = "디렉토리명", examples = {
            @ExampleObject(name = "example1", value = "myDir"),
            @ExampleObject(name = "example2", value = "myDir/subDir")
        }) @RequestParam("dirName") String dirName,

        @Parameter(description = "파일명", example = "myFile")
        @RequestParam("fileName") String fileName,

        @Parameter(description = "확장자명", example = "png")
        @RequestParam("extension") String extension) {
        String presignedUrl = s3Service.generatePresignedUrl(dirName, fileName, extension);
        String fileUrl = s3Service.extractFileUrl(presignedUrl);
        PresignedUrlResponseDto presignedUrlResponseDto = new PresignedUrlResponseDto(presignedUrl,
            fileUrl);
        SuccessResponseDto<PresignedUrlResponseDto> response = SuccessResponseDto.create(
            presignedUrlResponseDto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "파일 삭제",
        description = "S3에서 파일을 삭제합니다. 전체 URL을 파라미터로 받아 파일을 삭제합니다. 반환 데이터(data)는 없습니다."
    )
    public ResponseEntity<SuccessResponseDto<String>> deleteFile(
        @RequestParam("fileUrl") String fileUrl) {
        s3Service.deleteFileByUrl(fileUrl);
        SuccessResponseDto<String> response = SuccessResponseDto.create(null);

        return ResponseEntity.ok(response);
    }
}