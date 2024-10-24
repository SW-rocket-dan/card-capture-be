package app.cardcapture.template.service;

import static app.cardcapture.common.dto.ErrorCode.RETRIEVAL_FAILED;

import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.template.dto.TemplateOpenSearchResponseDto;
import app.cardcapture.template.dto.TemplateSearchResponseDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemplateSearchService {

    private static final String INDEX = "templates";
    private final OpenSearchClient openSearchClient;

    public TemplateSearchResponseDto searchByTitleField(String query) {
        Query termQuery = buildTitleFieldQuery(query);
        SearchRequest searchRequest = buildSearchRequest(termQuery);

        List<TemplateOpenSearchResponseDto> resultList = executeSearch(searchRequest);
        return new TemplateSearchResponseDto(resultList);
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
            throw new BusinessLogicException(RETRIEVAL_FAILED);
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
