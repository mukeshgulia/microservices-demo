package com.microservices.demo.elastic.query.client.service.impl;

import com.microservices.demo.common.util.CollectionsUtil;
import com.microservices.demo.elastic.model.index.impl.TwitterIndexModel;
import com.microservices.demo.elastic.query.client.exception.ElasticQueryClientException;
import com.microservices.demo.elastic.query.client.repository.TwitterElasticSearchQueryRepository;
import com.microservices.demo.elastic.query.client.service.ElasticQueryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Primary
@Service
public class TwitterElasticRepositoryClientImpl implements ElasticQueryClient<TwitterIndexModel> {

    private final TwitterElasticSearchQueryRepository twitterElasticSearchQueryRepository;

    public TwitterElasticRepositoryClientImpl(
            TwitterElasticSearchQueryRepository twitterElasticSearchQueryRepository) {
        this.twitterElasticSearchQueryRepository = twitterElasticSearchQueryRepository;
    }

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Optional<TwitterIndexModel> searchResult = twitterElasticSearchQueryRepository.findById(id);
        log.info("Document with id {} retrieved successfully",
                searchResult.orElseThrow(()
                                -> new ElasticQueryClientException("No document found at elasticsearch with id " + id)).getId());
        return searchResult.get();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        List<TwitterIndexModel> searchResult = twitterElasticSearchQueryRepository.findByText(text);
        log.info("{} of document with text {} retrieved successfully", searchResult.size(), text);
        return searchResult;
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        Iterable<TwitterIndexModel> searchResultIterable = twitterElasticSearchQueryRepository.findAll();
        CollectionsUtil instance = CollectionsUtil.getInstance();
        List<TwitterIndexModel> searchResult = instance.getListFromIterable(searchResultIterable);
        log.info("{} of document retrieved successfully", searchResult.size());
        return searchResult;
    }
}
