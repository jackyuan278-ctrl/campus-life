package com.campuslife.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ActivityRepository extends ElasticsearchRepository<ActivityDoc, Long> {
}
