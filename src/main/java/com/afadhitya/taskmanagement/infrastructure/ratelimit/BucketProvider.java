package com.afadhitya.taskmanagement.infrastructure.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BucketProvider {

    private final Cache<String, Bucket> bucketCache;
    private final Bandwidth defaultBandwidth;
    private final Bandwidth apiKeyBandwidth;

    public Bucket getDefaultBucket(String key) {
        return bucketCache.get(key, k -> Bucket.builder()
            .addLimit(defaultBandwidth)
            .build());
    }

    public Bucket getApiKeyBucket(String key) {
        return bucketCache.get(key, k -> Bucket.builder()
            .addLimit(apiKeyBandwidth)
            .build());
    }
}
