import org.feedsearch.manager.BloomFilterManager;
import org.feedsearch.model.Feed;
import org.feedsearch.repository.FeedRepository;
import org.feedsearch.service.FeedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private BloomFilterManager bloomFilterManager;

    @Mock
    private GridFsTemplate gridFsTemplate;

    @InjectMocks
    private FeedService feedService;

    @Test
    void testSaveFeed() {
        Feed feed = Feed.builder().title("Feed1").build();
        when(feedRepository.save(any())).thenReturn(feed);

        Feed saved = feedService.saveFeed(feed);

        assertEquals("Feed1", saved.getTitle());
        verify(bloomFilterManager).addToFilter("Feed1");
    }

    @Test
    void testSearchFeedsByTitleFound() {
        when(bloomFilterManager.mightContain("feed"))
                .thenReturn(true);
        when(feedRepository.findByTitleContainingIgnoreCase("feed"))
                .thenReturn(List.of(new Feed()));

        List<Feed> result = feedService.searchFeedsByTitle("feed");

        assertEquals(1, result.size());
    }

    @Test
    void testSearchFeedsByTitleNotInBloom() {
        when(bloomFilterManager.mightContain("notfound")).thenReturn(false);
        List<Feed> result = feedService.searchFeedsByTitle("notfound");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGenerateBulkFeeds() {
        when(feedRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        List<Feed> result = feedService.generateBulkTestFeeds(5);

        assertEquals(5, result.size());
    }
}
