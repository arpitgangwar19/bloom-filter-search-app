package org.feedsearch.manager;


import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class BloomFilterManager {

    private BloomFilter<String> bloomFilter;

    @PostConstruct
    public void init() {
        // Expected inserts and acceptable false positive rate
        bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                5000, // expected inserts
                0.01 // 1% false positive rate
        );
    }

    public void addToFilter(String value) {
        bloomFilter.put(value);
    }

    public boolean mightContain(String value) {
        return bloomFilter.mightContain(value);
    }
}
