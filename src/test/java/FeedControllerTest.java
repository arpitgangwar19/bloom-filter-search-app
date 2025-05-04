import org.feedsearch.FeedSearchApp;
import org.feedsearch.controller.FeedController;
import org.feedsearch.model.Feed;
import org.feedsearch.repository.FeedRepository;
import org.feedsearch.service.FeedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FeedController.class)
@ContextConfiguration(classes = FeedSearchApp.class)
class FeedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedService feedService;

    @MockBean
    private FeedRepository feedRepository;

    @MockBean
    private GridFsTemplate gridFsTemplate;

    @Test
    void testCreateFeed() throws Exception {
        Feed feed = Feed.builder().title("Test").build();
        when(feedService.saveFeed(any())).thenReturn(feed);

        mockMvc.perform(post("/api/feeds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test"));
    }

    @Test
    void testSearchFeed() throws Exception {
        when(feedService.searchFeedsByTitle("hello")).thenReturn(List.of(new Feed()));

        mockMvc.perform(get("/api/feeds/search?title=hello"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllFeeds() throws Exception {
        when(feedService.getAllFeeds()).thenReturn(List.of(new Feed()));
        mockMvc.perform(get("/api/feeds"))
                .andExpect(status().isOk());
    }

    @Test
    void testBulkFeed() throws Exception {
        when(feedService.generateBulkTestFeeds(3)).thenReturn(List.of(new Feed(), new Feed(), new Feed()));

        mockMvc.perform(post("/api/feeds/bulk-test?count=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void testUploadFeedLocal() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "file.jpg", "image/jpeg", "dummy".getBytes());

        Feed feed = Feed.builder().title("Uploaded").build();
        when(feedService.saveFeedWithLocalFile(any(), anyString(), anyString(), any())).thenReturn(feed);

        mockMvc.perform(multipart("/api/feeds/upload")
                        .file(file)
                        .param("title", "Uploaded")
                        .param("description", "desc")
                        .param("tags", "tag1", "tag2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Uploaded"));
    }
}
