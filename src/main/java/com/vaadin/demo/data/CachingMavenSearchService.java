package com.vaadin.demo.data;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class CachingMavenSearchService extends MavenSearchService {

    private static Cache<String, MavenSearchResponse> cache;

    static {
        cache = CacheBuilder.newBuilder().maximumSize(100)
                .expireAfterWrite(30, TimeUnit.MINUTES).build();
    }

    @Override
    public MavenSearchResponse search(final String searchTerms,
            final int start, final int rows) {
        try {
            String cacheKey = searchTerms + "____" + start + "____" + rows;
            return cache.get(cacheKey, new Callable<MavenSearchResponse>() {

                @Override
                public MavenSearchResponse call() throws Exception {
                    // Do the actual REST query.
                    return CachingMavenSearchService.super.search(searchTerms,
                            start, rows);
                }

            });
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
