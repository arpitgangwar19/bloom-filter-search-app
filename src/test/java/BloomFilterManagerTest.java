import org.feedsearch.FeedSearchApp;
import org.feedsearch.manager.BloomFilterManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = FeedSearchApp.class)
class BloomFilterManagerTest {

    @Autowired
    private BloomFilterManager bloomFilterManager;

    @Test
    void testAddAndCheck() {
        String value = "Unique Title";
        bloomFilterManager.addToFilter(value);
        assertTrue(bloomFilterManager.mightContain(value));
    }

    @Test
    void testMightContainFalse() {
        assertFalse(bloomFilterManager.mightContain("Nonexistent Title"));
    }
}