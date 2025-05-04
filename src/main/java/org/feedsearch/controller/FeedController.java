package org.feedsearch.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.feedsearch.model.Feed;
import org.feedsearch.repository.FeedRepository;
import org.feedsearch.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {

    @Autowired
    private  FeedService feedService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private FeedRepository feedRepository;

    @PostMapping
    public Feed createFeed(@RequestBody Feed feed) {
        return feedService.saveFeed(feed);
    }

    @GetMapping("/search")
    public List<Feed> searchFeeds(@RequestParam String title) {
        return feedService.searchFeedsByTitle(title);
    }

    @GetMapping
    public List<Feed> getAllFeeds() {
        return feedService.getAllFeeds();
    }

    @PostMapping("/upload")
    public Feed uploadFeed(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam List<String> tags,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {
        return feedService.saveFeedWithLocalFile(imageFile, title, description, tags);
    }

    @PostMapping("/bulk-test")
    public List<Feed> generateTestFeeds(@RequestParam(defaultValue = "10") int count) {
        return feedService.generateBulkTestFeeds(count);
    }

    @PostMapping("/upload/gridfs")
    public Feed uploadWithGridFs(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam List<String> tags,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {
        return feedService.saveFeedWithImage(imageFile, title, description, tags);
    }

    @GetMapping("/image/{feedId}")
    public ResponseEntity<?> getImageByFeedId(@PathVariable String feedId) throws IOException {
        return feedService.getImageByFeedId(feedId);
    }



}
