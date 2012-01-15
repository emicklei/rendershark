package org.rendershark.http;

import java.util.HashMap;
import java.util.Set;

import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.HttpRequest;
// http://www.hccp.org/java-net-cookie-how-to.html
public class HttpCookiesReadAccess extends HttpHeaderReadAccess {

    private HashMap<String,Cookie> requestCookies = null; // lazy init
    
    public HttpCookiesReadAccess(HttpRequest request) {
        super(request);
    }
    @Override
    public Object getObject(String key, Object... optional) {
        if (requestCookies == null) {
            this.readCookies();
        }
        return requestCookies.get(key);
    }
    private void readCookies() {
       requestCookies = new HashMap<String,Cookie>(); 
       String cookieString = (String)request.getHeader("Cookie");
       if (cookieString != null) {
           CookieDecoder decoder = new CookieDecoder();
           Set<Cookie> cookies = decoder.decode(cookieString);
           for (Cookie each : cookies) {
               requestCookies.put(each.getName(),each);
           }
       }
    }    
}
