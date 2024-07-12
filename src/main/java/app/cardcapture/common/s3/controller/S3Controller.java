package app.cardcapture.common.s3.controller;

import app.cardcapture.common.s3.dto.PresignedUrlResponseDto;
import app.cardcapture.common.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
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
    public ResponseEntity<PresignedUrlResponseDto> generatePresignedUrl(
            @Parameter(description = "디렉토리명", examples = {
                    @ExampleObject(name = "example1", value = "myDir"),
                    @ExampleObject(name = "example2", value = "myDir/subDir")
            }) @RequestParam("dirName") String dirName,

            @Parameter(description = "파일명", example = "myFile")
            @RequestParam("fileName") String fileName,

            @Parameter(description = "확장자명", example = "png")
            @RequestParam("extension") String extension) {
        try {
            String presignedUrl = s3Service.generatePresignedUrl(dirName, fileName, extension);
            String fileUrl = s3Service.extractFileUrl(presignedUrl);
            PresignedUrlResponseDto response = new PresignedUrlResponseDto(presignedUrl, fileUrl);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("프리사인 URL 발급 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(500).body(new PresignedUrlResponseDto(null, null));
        }
    }


    @DeleteMapping("/delete")
    @Operation(
            summary = "파일 삭제",
            description = "S3에서 파일을 삭제합니다. 전체 URL을 파라미터로 받아 파일을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "파일이 삭제되었습니다."),
                    @ApiResponse(responseCode = "500", description = "파일 삭제 중 오류 발생")
            }
    )
    public ResponseEntity<String> deleteFile(
            @Parameter(description = "파일 URL", example = "https://your-s3-bucket.s3.amazonaws.com/test/testFile.png") @RequestParam("fileUrl") String fileUrl) {
        try {
            s3Service.deleteFileByUrl(fileUrl);
            return ResponseEntity.ok("파일이 삭제되었습니다.");
        } catch (Exception e) {
            log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(500).body("파일 삭제 중 오류가 발생했습니다.");
        }
    }
}
