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
                    "png같은 확장자를 정해줘야 합니다.",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Generate Presigned URL Request",
                                    value = "{\n" +
                                            "  \"dirName\": \"test\",\n" +
                                            "  \"fileName\": \"testFile\",\n" +
                                            "  \"extension\": \"png\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "프리사인 URL 생성 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Generate Presigned URL Response",
                                            value = "{\n" +
                                                    "  \"presignedUrl\": \"https://cardcaptureposterimage.s3.ap-northeast-2.amazonaws.com/test/81059369-2511-4c5c-b4ca-5ae2726c5612_test1.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20240710T055837Z&X-Amz-SignedHeaders=host&X-Amz-Expires=600&X-Amz-Credential=AKIA3FLDXCLAJLR7VTGK%2F20240710%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=f8feca7495eeeb7218c2c4717989e6c1701636bc45f3fdb035a03828ca154733\",\n" +
                                                    "  \"fileUrl\": \"https://cardcaptureposterimage.s3.ap-northeast-2.amazonaws.com/test/81059369-2511-4c5c-b4ca-5ae2726c5612_test1.png\"\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "프리사인 URL 발급 중 오류 발생",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Error Response",
                                            value = "{\n" +
                                                    "  \"message\": \"프리사인 URL 발급 중 오류가 발생했습니다.\"\n" +
                                                    "}"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<PresignedUrlResponseDto> generatePresignedUrl(
            @RequestParam("dirName") String dirName,
            @RequestParam("fileName") String fileName,
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
