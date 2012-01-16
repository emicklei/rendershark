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
    public boolean isHandled = true;
    public Set<HttpHeader> responseHeaders = null; // lazy initialize
    public Set<Cookie> cookies = null; // lazy initialize

    public HandlerResult addHeader(String headerName, String headerValue) {
        if (responseHeaders == null) {
            responseHeaders = new HashSet<HttpHeader>();
            // add the default ; may be overwritten
            responseHeaders.add(new HttpHeader("Content-Type","text/html;charset=UTF-8"));
        }
        responseHeaders.add(new HttpHeader(headerName,headerValue));
        return this;
    }
    
    public HandlerResult addCookie(Cookie newCookie) {
        if (cookies == null) {
            cookies = new HashSet<Cookie>();
        }
        cookies.add(newCookie);
        return this;
    }
    
    public HandlerResult redirectTo(String url) {
        this.redirectUrl = url;
        return this;
    }
    public HandlerResult forwardTo(String url) {
        this.forwardUrl = url;
        return this;
    }

    public boolean isRedirect() {
        return redirectUrl != null;
    }

    public boolean isForward() {
        return forwardUrl != null;
    }    
    public boolean isHandled() { return isHandled; }
    
    public boolean hasHeaders() { return responseHeaders != null; }

    public boolean hasCookies() { return cookies != null; }
    
    public Set<HttpHeader> getHeaders() { return responseHeaders; }

    public void unhandled() { isHandled = false; }
}