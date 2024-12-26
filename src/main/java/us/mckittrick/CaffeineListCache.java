package us.mckittrick;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CaffeineListCache {
    private Cache<String, String> cache;

    public CaffeineListCache(Integer maxCacheSize) {
        cache = Caffeine.newBuilder()
                .maximumSize(maxCacheSize) // Maximum number of entries
                .build();
    }

    public void put(String key) {
        cache.put(key, "");
    }

    public Boolean checkCache(String key) {
        if (cache.getIfPresent(key) != null) {
            return true;
        } else {
            return false;
        }
    }
}
