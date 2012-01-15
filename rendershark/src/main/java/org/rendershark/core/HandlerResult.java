package org.rendershark.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.handler.codec.http.Cookie;
/**
 * HandlerResult captures the result of handling a GET of POST by an implementor of HttpGetHandler or HttpPostHandler respectively.
 * Using this object you can tell the dispatcher to:
 * <ul>
 * <li>redirect the client to a different URL</li>
 * <li>forward the request to a handler on a different URL</li>
 * <li>process the situation that no handler was found for the request</li>
 * </ul>
 * A HandlerResult is also the container for:
 * <ul>
 * <li>additional HTTP headers for the response to send back</li>
 * <li>new Cookies for the response to send back (in Set-Cookie headers)</li>
 * </ul>
 * @author ernestmicklei
 */
public class HandlerResult {
    public String redirectUrl;
    public String forwardUrl;
    public boolean isHandled = false;
    public Map<String,String> headers = null; // lazy initialize
    public Set<Cookie> cookies = null; // lazy initialize

    public HandlerResult addHeader(String headerName, String headerValue) {
        if (headers == null) {
            headers = new HashMap<String,String>();
            // add the default ; may be overwritten
            headers.put("Content-Type","text/html; charset=UTF-8");
        }
        headers.put(headerName,headerValue);
        return this;
    }
    
    public HandlerResult addCookie(Cookie newCookie) {
        if (cookies == null) {
            cookies = new HashSet<Cookie>();
        }
        cookies.add(newCookie);
        return this;
    }
    
    public static HandlerResult redirectTo(String url) {
        HandlerResult r = new HandlerResult();
        r.redirectUrl = url;
        return r;
    }
    public static HandlerResult forwardTo(String url) {
        HandlerResult r = new HandlerResult();
        r.forwardUrl = url;
        return r;
    }

    public static HandlerResult ok() {
        return new HandlerResult().beHandled();
    }

    public static HandlerResult unhandled() {
        return new HandlerResult();
    }
    
    public boolean isRedirect() {
        return redirectUrl != null;
    }

    public boolean isForward() {
        return forwardUrl != null;
    }
    
    public HandlerResult beHandled() {
        isHandled = true;
        return this;
    }
    public boolean isHandled() { return isHandled; }
    
    public boolean hasHeaders() { return headers != null; }

    public boolean hasCookies() { return cookies != null; }
    
    public Map<String,String> getHeaders() { return Collections.unmodifiableMap(headers); }
}