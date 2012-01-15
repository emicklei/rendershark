package org.rendershark.http.session;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.infinispan.Cache;
import org.infinispan.config.Configuration;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SessionManager {	
	private static final String SESSION_CACHE_NAME = "session-cache";
    private static final Logger LOG = LoggerFactory.getLogger(SessionManager.class);
    private Cache<Object,Object> sessionCache; // see init
        
	@SuppressWarnings("unused")
	@Inject
	private void init() {
		LOG.info("Initializing Global Config Infinispan");
		GlobalConfiguration globalConfig = GlobalConfiguration.getNonClusteredDefault();
		globalConfig.fluent().globalJmxStatistics().build();

		LOG.info("Initializing Session Caching");		
		EmbeddedCacheManager manager = new DefaultCacheManager(globalConfig);
		Configuration config = new Configuration().fluent()
			.expiration().maxIdle(30*60*1000L) // 30 minutes
			.jmxStatistics()
			.build();
		manager.defineConfiguration(SESSION_CACHE_NAME, config);
		this.sessionCache = manager.getCache(SESSION_CACHE_NAME);
	}
    
	public void clearSession(String id) {
	    sessionCache.remove(id);
	}
	
    public HttpSession getSession(String idOrNull, boolean createIfAbsent) {
        HttpSession session = null;
        if (idOrNull != null) {
            session = (HttpSession) sessionCache.get(idOrNull);
        }
        if (session == null) {
            session = new HttpSession(this);
            sessionCache.put(session.getId(), session);
        }
        return session;
    }
}
