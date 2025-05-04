package org.feedsearch.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("feeds")
public class Feed {
    @Id
    private String id;

    private String title;
    private String description;
    private List<String> tags;
    private String imageUrl;
    private String imageFileId;
    private String imageFilename;
    private LocalDateTime createdAt;
}