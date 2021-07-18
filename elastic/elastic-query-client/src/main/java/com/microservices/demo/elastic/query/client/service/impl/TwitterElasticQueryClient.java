package com.microservices.demo.elastic.query.client.service.impl;

import com.microservices.demo.config.ElasticConfigData;
import com.microservices.demo.config.ElasticQueryConfigData;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.client.exception.ElasticQueryClientException;
import com.microservices.demo.elastic.query.client.util.ElasticQueryUtil;
import com.microservices.demo.elastic.query.client.service.ElasticQueryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TwitterElasticQueryClient implements ElasticQueryClient<TwitterIndexModel> {

    private final ElasticConfigData elasticConfigData;
    private final ElasticQueryConfigData elasticQueryConfigData;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticQueryUtil<TwitterIndexModel> elasticQueryUtil;

    public TwitterElasticQueryClient(ElasticConfigData elasticConfigData,
                                     ElasticQueryConfigData elasticQueryConfigData,
                                     ElasticsearchOperations elasticsearchOperations,
                                     ElasticQueryUtil<TwitterIndexModel> elasticQueryUtil) {
        this.elasticConfigData = elasticConfigData;
        this.elasticQueryConfigData = elasticQueryConfigData;
        this.elasticsearchOperations = elasticsearchOperations;
        this.elasticQueryUtil = elasticQueryUtil;
    }

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Query searchQueryById = elasticQueryUtil.getSearchQueryById(id);
        SearchHit<TwitterIndexModel> searchHit = elasticsearchOperations.searchOne(searchQueryById, TwitterIndexModel.class,
                IndexCoordinates.of(elasticConfigData.getIndexName()));
        if (searchHit == null) {
            log.info("No document found at elasticsearch with id {}", id);
            throw new ElasticQueryClientException("No document found at elasticsearch with id " + id);
        }
        log.info("Document with id {} retrieved successfully", id);
        return searchHit.getContent();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        Query query = elasticQueryUtil.getSearchQueryByFieldAText(elasticQueryConfigData.getText(), text);
        return search(query, "{} of document with text {} retrieved successfully", text);
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        Query query = elasticQueryUtil.getSearchQueryForAll();
        return search(query, "{} number of documents retrieved successfully");
    }

    private List<TwitterIndexModel> search(Query query, String logMessage, Object... logParams) {
        SearchHits<TwitterIndexModel> hits = elasticsearchOperations.search(query, TwitterIndexModel.class,
                IndexCoordinates.of(elasticConfigData.getIndexName()));
        log.info(logMessage, hits.getTotalHits(), logParams);
        return hits.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }
}
