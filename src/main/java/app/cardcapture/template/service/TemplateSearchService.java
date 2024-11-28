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
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
    private final ConcurrentHashMap<Long, TemplateUpdateRequestDto> updateMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void initScheduler() {
        scheduler.scheduleAtFixedRate(this::processBatchUpdates, 1, 1, TimeUnit.SECONDS);
    }

    public void collectUpdate(TemplateUpdateRequestDto templateUpdateRequestDto) {
        System.out.println(updateMap);
        updateMap.compute(templateUpdateRequestDto.id(), (key, existingValue) -> {
            if (existingValue == null) {
                return templateUpdateRequestDto;
            }
            if (existingValue.createdAt().isBefore(templateUpdateRequestDto.createdAt())) {
                return templateUpdateRequestDto;
            }
            return existingValue;
        });
    }

    private void processBatchUpdates() {
        if (updateMap.isEmpty()) {
            return;
        }

        updateMap.forEach((id, updateRequestDto) -> {
            try {
                Template template = templateRepository.findById(updateRequestDto.id())
                    .orElseThrow(() -> new BusinessLogicException(ErrorCode.NOT_FOUND));

                ObjectNode updateJson = objectMapper.createObjectNode();
                Set<TemplateAttribute> updatedAttributes = updateRequestDto.updatedAttributes();

                if (updatedAttributes.contains(TemplateAttribute.TITLE)) {
                    updateJson.put("title", updateRequestDto.title());
                }
                if (updatedAttributes.contains(TemplateAttribute.DESCRIPTION)) {
                    updateJson.put("description", updateRequestDto.description());
                }
                if (updatedAttributes.contains(TemplateAttribute.FILE_URL)) {
                    updateJson.put("fileUrl", updateRequestDto.fileUrl());
                }
                if (updatedAttributes.contains(TemplateAttribute.EDITOR)) {
                    updateJson.put("editor", updateRequestDto.editor());
                }

                UpdateRequest updateRequest = new UpdateRequest.Builder()
                    .index(INDEX)
                    .id(String.valueOf(template.getId()))
                    .doc(updateJson)
                    .build();

                openSearchClient.update(updateRequest, JsonNode.class);

            } catch (Exception e) {
                throw new BusinessLogicException(ErrorCode.SERVER_ERROR);
            } finally {
                updateMap.remove(id);
            }
        });
    }

    public TemplateSearchResponseDto searchByTitleField(String query) {
        Query termQuery = buildTitleFieldQuery(query);
        SearchRequest searchRequest = buildSearchRequest(termQuery);

        List<TemplateOpenSearchResponseDto> resultList = executeSearch(searchRequest);
        return new TemplateSearchResponseDto(resultList);
    }

    public void update(TemplateUpdateRequestDto templateUpdateRequestDto) {
        Template template = templateRepository.findById(templateUpdateRequestDto.id())
            .orElseThrow(()
                -> new BusinessLogicException(ErrorCode.NOT_FOUND));

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
