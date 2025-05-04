package org.feedsearch.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.feedsearch.manager.BloomFilterManager;
import org.feedsearch.model.Feed;
import org.feedsearch.repository.FeedRepository;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;

    private final BloomFilterManager bloomFilterManager;

    private final GridFsTemplate gridFsTemplate;

    public Feed saveFeed(Feed feed) {
        Feed savedFeed = feedRepository.save(feed);
        bloomFilterManager.addToFilter(savedFeed.getTitle());
        return savedFeed;
    }

    public List<Feed> searchFeedsByTitle(String title) {
        if (bloomFilterManager.mightContain(title)) {
            return feedRepository.findByTitleContainingIgnoreCase(title);
        }
        return List.of(); // empty list if not possibly contained
    }

    public List<Feed> getAllFeeds() {
        return feedRepository.findAll();
    }

    public Feed saveFeedWithImage(MultipartFile imageFile, String title, String description, List<String> tags) throws IOException {
        ObjectId fileId = gridFsTemplate.store(imageFile.getInputStream(), imageFile.getOriginalFilename(), imageFile.getContentType());

        Feed feed = Feed.builder()
                .title(title)
                .description(description)
                .tags(tags)
                .imageFileId(fileId.toHexString())
                .createdAt(LocalDateTime.now())
                .build();

        return feedRepository.save(feed);
    }

    public Feed saveFeedWithLocalFile(MultipartFile imageFile, String title, String description, List<String> tags) throws IOException {
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path uploadPath = Paths.get("uploads");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Feed feed = Feed.builder()
                .title(title)
                .description(description)
                .tags(tags)
                .imageFilename(fileName)
                .createdAt(LocalDateTime.now())
                .build();

        return feedRepository.save(feed);
    }

    public List<Feed> generateBulkTestFeeds(int count) {
        List<Feed> feeds = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Feed feed = Feed.builder()
                    .title("Test Feed " + i)
                    .description("Description for feed " + i)
                    .tags(List.of("test", "bulk", "demo"))
                    .imageUrl("http://dummyimage.com/feed" + i + ".jpg")
                    .createdAt(LocalDateTime.now().minusDays(i))
                    .build();
            feeds.add(feedRepository.save(feed));
        }
        return feeds;
    }

    public ResponseEntity<?> getImageByFeedId(String feedId) throws IOException {
        Feed feed = feedRepository.findById(feedId).orElse(null);
        if (feed == null || feed.getImageFileId() == null) {
            return ResponseEntity.notFound().build();
        }

        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(feed.getImageFileId()))));
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        GridFsResource resource = gridFsTemplate.getResource(file);
        String contentType = Optional.ofNullable(file.getMetadata())
                .map(m -> m.getString("_contentType"))
                .orElse("application/octet-stream");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource.getInputStream().readAllBytes());
    }
}