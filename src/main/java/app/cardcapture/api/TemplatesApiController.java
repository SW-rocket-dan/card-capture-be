package app.cardcapture.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "templates", description = "The templates API")
@RestController
@RequestMapping("/api/v1")
public class TemplatesApiController {

    static class Structure {
        public String key;
        public String value;

        public Structure(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    static class Template {
        public Long id;
        public int star;
        public int price;
        public Structure structure;
        public String imageUrl;
        public LocalDateTime createdAt;
        public LocalDateTime updatedAt;

        public Template(long id, int star, int price,
                        Structure structure, String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.star = star;
            this.price = price;
            this.structure = structure;
            this.imageUrl = imageUrl;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
    }

    @GetMapping("/templates")
    @Operation(summary = "검색어 기반 템플릿 여러 개 조회",
            description = "검색어에 따라 템플릿 목록을 조회합니다. " +
                    "커서 기반으로 다음 페이지를 조회할 수 있습니다. " +
                    "star, createdAt, updatedAt, id 순으로 정렬할 수 있습니다. " +
                    "검색어가 없을 경우에도 무언가 템플릿들을 보여줄 것입니다. " +
                    "첫 조회 페이지의 경우 cursor값이 없습니다. " +
                    "마지막 페이지는 cursor값이 null입니다. " +
                    "페이지 당 보여줄 템플릿 개수를 limit로 정해줄 수 있습니다. limit의 범위는 1~100입니다.")
    @ApiResponse(responseCode = "200", description = "템플릿 목록 조회 성공",
            content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Template.class)))})
    public List<Template> getTemplates(
            @Parameter(description = "검색어", example = "커피")
            @RequestParam(required = false, defaultValue = "") String query,

            @Parameter(description = "커서", example = "ekJpXCI6MX")
            @RequestParam(required = false, defaultValue = "-1") String cursor,

            @Parameter(description = "템플릿 개수", example = "10",
                    schema = @Schema(type = "integer", format = "int32", minimum = "1", maximum = "100", defaultValue = "10"))
            @RequestParam(required = false, defaultValue = "10") int limit
    ) {
        return List.of(
                new Template(1L,
                        5,
                        1000,
                        new Structure("key", "value"),
                        "https://example.com/image1.jpg",
                        LocalDateTime.of(2023, 04, 21, 12, 58),
                        LocalDateTime.of(2023, 04, 23, 17, 2)),
                new Template(2L,
                        4,
                        2000,
                        new Structure("key", "value"),
                        "https://example.com/image2.jpg",
                        LocalDateTime.of(2024, 05, 7, 9, 35),
                        LocalDateTime.of(2023, 06, 20, 1, 48))
        );
    }


}
