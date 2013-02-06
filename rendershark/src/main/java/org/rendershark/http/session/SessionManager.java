package org.rendershark.http.session;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;

@Singleton
public class SessionManager {	
    private static final Logger LOG = LoggerFactory.getLogger(SessionManager.class);
    private com.google.common.cache.Cache<Object,Object> sessionCache; // see init
        
	@Inject
	private void init() {
	    LOG.info("Initializing Session Caching");
	    sessionCache = CacheBuilder.newBuilder()
	            .expireAfterAccess(10, TimeUnit.MINUTES) // TODO config this
	            .build();
	}
    
	public void clearSession(String id) {
	    sessionCache.invalidateAll();
	}
	
    public HttpSession getSession(String idOrNull, boolean createIfAbsent) {
        HttpSession session = null;
        if (idOrNull != null) {
            session = (HttpSession) sessionCache.getIfPresent(idOrNull);
        }
        if (session == null) {
            session = new HttpSession(this);
            sessionCache.put(session.getId(), session);
        }
        return session;
    }
}
