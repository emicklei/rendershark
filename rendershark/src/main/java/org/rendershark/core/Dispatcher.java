package org.rendershark.core;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.rendershark.core.error.ErrorConstants;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.PageContext;
import org.rendersnake.internal.ContextMap;
import org.rendersnake.internal.SimpleContextMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.google.inject.Key;

@Singleton
public class Dispatcher {
    private static final Logger LOG = LoggerFactory.getLogger(Dispatcher.class);

    private HttpGetHandler defaultErrorAction;
    
    public Dispatcher(HttpGetHandler errorAction) {
        super();
        this.defaultErrorAction = errorAction;
    }
    
    private Map<String, HttpGetHandler> getHandlerMap = new HashMap<String, HttpGetHandler>();
    private Map<String, HttpPostHandler> postHandlerMap = new HashMap<String, HttpPostHandler>();

    public HandlerResult handleGet(URI uri, HtmlCanvas canvas) throws IOException {
        HttpGetHandler handler = getHandlerMap.get(uri.getPath());
        if (handler == null) {
            // Try pattern match
            for (Map.Entry<String,HttpGetHandler> each : getHandlerMap.entrySet()) {
                // Pick the first match
                if (this.canMapUriTo(
                        uri,
                        each.getKey(),
                        canvas.getPageContext())) {
                    handler = each.getValue();
                    break;
                }
            }
        }         
        if (handler == null) {
            LOG.warn("No HttpGetHandler declared for handling GET [" + uri + "]");
            return HandlerResult.unhandled();
        } else {
            return handler.get(canvas);
        }
    }

    public HandlerResult handlePost(URI uri, HtmlCanvas canvas) throws IOException  {
        HttpPostHandler handler = postHandlerMap.get(uri.getPath());
        if (handler == null) {
            // Try pattern match
            for (Map.Entry<String,HttpPostHandler> each : postHandlerMap.entrySet()) {
                // Pick the first match
                if (this.canMapUriTo(
                        uri,
                        each.getKey(),
                        canvas.getPageContext())) {
                    handler = each.getValue();
                    break;
                }
            }
        }        
        if (handler == null) {
            LOG.warn("No HttpPostHandler declared for handling POST [" + uri + "]");
            return HandlerResult.unhandled();
        } else {            
            return handler.post(canvas);
        }
    }
    
    private boolean canMapUriTo(URI uri, String key, PageContext context) {
        //  /page/{id}/something  --- /page/1232/something
        String[] uriSegments = uri.getPath().split("/");
        String[] keySegments = key.split("/");
        if (uriSegments.length != keySegments.length)
            return false;
        for(int s=0;s<uriSegments.length;s++) {
            String keySegment = keySegments[s];
            if (!keySegment.startsWith("{") && !keySegment.equals(uriSegments[s])) {
                return false;
            }
        }
        // TODO combine this
        ContextMap access = (ContextMap)context.getObject(PageContext.REQUEST_PATH,new SimpleContextMap());
        context.withObject(PageContext.REQUEST_PATH,access);
        for(int s=0;s<uriSegments.length;s++) {
            String keySegment = keySegments[s];            
            if (keySegment.startsWith("{")) {
                String param = keySegment.substring(1,keySegment.length()-1);    
                access.withObject(param,uriSegments[s]);
            }
        }        
        return true;
    }

    public boolean handleHead(URI uri) {
    	return getHandlerMap.get(uri) != null;
    }

    public void handleErrorStatus(URI uri, HttpResponseStatus status, HtmlCanvas canvas) throws IOException  {
    	String path = "/" + status.getCode() + ".html";
    	canvas.getPageContext().withObject(ErrorConstants.CONTEXT_URI, uri); // TODO constants
    	canvas.getPageContext().withObject(ErrorConstants.CONTEXT_STATUS, status);
    	HttpGetHandler handler = getHandlerMap.get(path);
        if (handler == null)
            handler = this.defaultErrorAction;
        handler.get(canvas);
    }
    
    @Inject
    public void init(Injector injector) {
        for (Key<?> key : injector.getBindings().keySet()) {
            Type type = key.getTypeLiteral().getType();
            if (type instanceof Class<?>) {
                Class<?> klass = (Class<?>) type;
                if (HttpGetHandler.class.isAssignableFrom(klass)) {
                    HttpGetHandler handler = (HttpGetHandler) injector.getInstance(klass);
                    Named annotation = klass.getAnnotation(Named.class);
                    if (annotation != null) {
                        this.register(handler, annotation.value());
                    } else {
                        LOG.warn("Missing @Named annotation in component:" + klass);
                    }
                }
                if (HttpPostHandler.class.isAssignableFrom(klass)) {
                    HttpPostHandler handler = (HttpPostHandler) injector.getInstance(klass);
                    Named annotation = klass.getAnnotation(Named.class);
                    if (annotation != null) {
                        this.register(handler, annotation.value());
                    } else {
                        LOG.warn("Missing @Named annotation in component:" + klass);
                    }
                }                
            }
        }
    }

    void register(HttpGetHandler p, String uri) {
        if (getHandlerMap.containsKey(uri)) {
            HttpGetHandler existing = getHandlerMap.get(uri);
            LOG.warn("Duplicate mapping for [" + uri + "] , new: [" + p + "] old: [" + existing + "]");
        } else {
            getHandlerMap.put(uri, p);
        }
        LOG.info("Mapping GET  [" + uri + "] to [" + p + "]");
    }
    void register(HttpPostHandler p, String uri) {
        if (postHandlerMap.containsKey(uri)) {
            HttpPostHandler existing = postHandlerMap.get(uri);
            LOG.warn("Duplicate mapping for [" + uri + "] , new: [" + p + "] old: [" + existing + "]");
        } else {
            postHandlerMap.put(uri, p);
        }
        LOG.info("Mapping POST [" + uri + "] to [" + p + "]");
    }        
}
