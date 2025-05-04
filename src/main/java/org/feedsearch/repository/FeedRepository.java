package org.feedsearch.repository;



import org.feedsearch.model.Feed;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FeedRepository extends MongoRepository<Feed, String> {
    List<Feed> findByTitleContainingIgnoreCase(String title);
    List<Feed> findByTagsIn(List<String> tags);
}
