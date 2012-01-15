package org.rendershark.test;

import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.config.Configuration;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.loaders.file.FileCacheStoreConfig;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.Ignore;
import org.junit.Test;


public class InfiniTest {
    @Test
    @Ignore
    public void testSimple() throws Exception {
        
        GlobalConfiguration globalConfig = GlobalConfiguration.getNonClusteredDefault();
        globalConfig.fluent().globalJmxStatistics().build();

        EmbeddedCacheManager manager = new DefaultCacheManager(globalConfig);
        Configuration config = new Configuration().fluent()
            .expiration().maxIdle(10*1000L) // 10 seconds
            .jmxStatistics()
            .loaders()
                // single instance, write-through, no warm-cache
                .shared(false).passivation(false).preload(false)
                .addCacheLoader(
                        new FileCacheStoreConfig()
                            .location("/tmp/inifi").streamBufferSize(1800)
                            .asyncStore()
                            .threadPoolSize(20)
                            .ignoreModifications(false)
                            .purgeSynchronously(false))
            .build();
        manager.defineConfiguration("session-cache", config);
        Cache<Object, Object> cache = manager.getCache("session-cache");        
        cache.put("eventjes", 42);
        System.out.println(cache.get("eventjes"));
        Thread.sleep(30 * 1000L);
        System.out.println(cache.get("eventjes"));
        
        
        cache.put("eventjes", "ok", 20L, TimeUnit.SECONDS, 20L, TimeUnit.SECONDS);        
        System.out.println(cache.get("eventjes"));
        Thread.sleep(15 * 1000L);
        System.out.println(cache.get("eventjes"));        
        
    }
}
