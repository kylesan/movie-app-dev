package com.example.movieevents.config;

import com.example.movieevents.document.MovieEventDocument;
import jakarta.annotation.PostConstruct;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchIndexInitializer {

    private final ElasticsearchOperations elasticsearchOperations;

    public ElasticsearchIndexInitializer(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @PostConstruct
    public void init() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(MovieEventDocument.class);
        if (!indexOps.exists()) {
            indexOps.create();
            indexOps.putMapping(indexOps.createMapping());
            System.out.println("Created Elasticsearch index for MovieEventDocument");
        }
    }
}