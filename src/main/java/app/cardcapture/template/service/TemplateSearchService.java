package app.cardcapture.template.service;

import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.template.domain.TemplateAttribute;
import app.cardcapture.template.domain.entity.Template;
import app.cardcapture.template.dto.TemplateOpenSearchResponseDto;
import app.cardcapture.template.dto.TemplateSearchResponseDto;
import app.cardcapture.template.dto.TemplateUpdateRequestDto;
import app.cardcapture.template.repository.TemplateRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.UpdateRequest;
import org.opensearch.client.opensearch.core.UpdateResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateSearchService {

    private static final String INDEX = "templates";
    private final OpenSearchClient openSearchClient;
    private final TemplateRepository templateRepository;

    public TemplateSearchResponseDto searchByTitleField(String query) {
        Query termQuery = buildTitleFieldQuery(query);
        SearchRequest searchRequest = buildSearchRequest(termQuery);

        List<TemplateOpenSearchResponseDto> resultList = executeSearch(searchRequest);
        return new TemplateSearchResponseDto(resultList);
    }

    public void update(TemplateUpdateRequestDto templateUpdateRequestDto) {
        Template template = templateRepository.findById(templateUpdateRequestDto.id())
            .orElseThrow(()
                -> new BusinessLogicException(ErrorCode.USER_RETRIEVAL_FAILED));

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode updateJson = objectMapper.createObjectNode();

        Set<TemplateAttribute> updatedAttributes = templateUpdateRequestDto.updatedAttributes();
        if (updatedAttributes.contains(TemplateAttribute.TITLE)) {
            updateJson.put("title", templateUpdateRequestDto.title());
        }

        if (updatedAttributes.contains(TemplateAttribute.DESCRIPTION)) {
            updateJson.put("description", templateUpdateRequestDto.description());
        }

        if (updatedAttributes.contains(TemplateAttribute.FILE_URL)) {
            updateJson.put("fileUrl", templateUpdateRequestDto.fileUrl());
        }

        if (updatedAttributes.contains(TemplateAttribute.EDITOR)) {
            updateJson.put("editor", templateUpdateRequestDto.editor());
        }

        UpdateRequest updateRequest = new UpdateRequest.Builder()
            .index(INDEX)
            .id(String.valueOf(template.getId()))
            .doc(updateJson)
            .build();

        try {
            UpdateResponse<JsonNode> updateResponse = openSearchClient.update(updateRequest,
                JsonNode.class);
        } catch (IOException e) {
            throw new BusinessLogicException(ErrorCode.SERVER_ERROR);
        }
    }

    private List<TemplateOpenSearchResponseDto> executeSearch(SearchRequest searchRequest) {
        List<TemplateOpenSearchResponseDto> resultList = new ArrayList<>();

        try {
            SearchResponse<TemplateOpenSearchResponseDto> searchResponse = openSearchClient.search(
                searchRequest, TemplateOpenSearchResponseDto.class);
            for (Hit<TemplateOpenSearchResponseDto> hit : searchResponse.hits().hits()) {
                resultList.add(hit.source());
            }
        } catch (IOException e) {
            throw new BusinessLogicException(ErrorCode.SERVER_ERROR);
        }

        return resultList;
    }

    private SearchRequest buildSearchRequest(Query termQuery) {
        SearchRequest searchRequest = new SearchRequest.Builder()
            .index(INDEX)
            .query(termQuery)
            .size(10)
            .build();
        return searchRequest;
    }

    private Query buildTitleFieldQuery(String query) {
        Query termQuery = new Query.Builder()
            .match(
                f -> f.field("title")
                    .query(q -> q.stringValue(query)))
            .build();
        return termQuery;
    }
}
