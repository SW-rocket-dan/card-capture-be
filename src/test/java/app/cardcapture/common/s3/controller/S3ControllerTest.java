package app.cardcapture.common.s3.controller;

import app.cardcapture.common.config.CorsConfig;
import app.cardcapture.common.s3.service.S3Service;
import app.cardcapture.auth.jwt.service.JwtComponent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.isEmptyOrNullString;

@WebMvcTest(controllers = S3Controller.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CorsConfig.class)
public class S3ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private S3Service s3Service;

    @MockBean
    private JwtComponent jwtComponent;

    @Test
    void testGeneratePresignedUrl() throws Exception {
        // given
        String dirName = "test";
        String fileName = "testFile";
        String extension = "png";
        String presignedUrl = "https://your-s3-bucket.s3.amazonaws.com/test/testFile.png?signature";
        String fileUrl = "https://your-s3-bucket.s3.amazonaws.com/test/testFile.png";

        when(s3Service.generatePresignedUrl(dirName, fileName, extension)).thenReturn(presignedUrl);
        when(s3Service.extractFileUrl(presignedUrl)).thenReturn(fileUrl);

        // when && then
        mockMvc.perform(post("/s3/generate-presigned-url")
                        .param("dirName", dirName)
                        .param("fileName", fileName)
                        .param("extension", extension)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.presignedUrl").value(presignedUrl))
                .andExpect(jsonPath("$.fileUrl").value(fileUrl));
    }

    @Test
    void testGeneratePresignedUrlError() throws Exception {
        // given
        String dirName = "test";
        String fileName = "testFile";
        String extension = "png";
        Exception exception = new RuntimeException("프리사인 URL 발급 중 오류 발생");

        when(s3Service.generatePresignedUrl(dirName, fileName, extension)).thenThrow(exception);

        // when && then
        mockMvc.perform(post("/s3/generate-presigned-url")
                        .param("dirName", dirName)
                        .param("fileName", fileName)
                        .param("extension", extension)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.presignedUrl", isEmptyOrNullString()))
                .andExpect(jsonPath("$.fileUrl", isEmptyOrNullString()));
    }

    @Test
    void testDeleteFile() throws Exception {
        // given
        String fileUrl = "https://your-s3-bucket.s3.amazonaws.com/test/testFile.png";

        // when && then
        mockMvc.perform(delete("/s3/delete")
                        .param("fileUrl", fileUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("파일이 삭제되었습니다."));
    }

    @Test
    void testDeleteFileError() throws Exception {
        // given
        String fileUrl = "https://your-s3-bucket.s3.amazonaws.com/test/testFile.png";
        Exception exception = new RuntimeException("파일 삭제 중 오류 발생");

        doThrow(exception).when(s3Service).deleteFileByUrl(fileUrl);

        // when && then
        mockMvc.perform(delete("/s3/delete")
                        .param("fileUrl", fileUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$").value("파일 삭제 중 오류가 발생했습니다."));
    }
}
