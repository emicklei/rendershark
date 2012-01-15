package org.rendershark.example.action;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.DefaultCookie;
import org.rendershark.core.HandlerResult;
import org.rendershark.core.HttpGetHandler;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.PageContext;
import org.rendersnake.internal.ContextMap;

@Singleton @Named("/cookie.html")
public class CookieAction implements HttpGetHandler {

    public HandlerResult get(HtmlCanvas html) throws IOException {
        
        ContextMap cookies = html.getPageContext().getContextMap(PageContext.REQUEST_COOKIES);
        Cookie a = (Cookie)cookies.getObject("a");
        HandlerResult result = HandlerResult.ok();
        if (a == null) {
            Cookie newCookie = new DefaultCookie("a","i am a cookie");
            //Cookie newCookie = new DefaultCookie("a","b");
            //newCookie.setPath("/");
            //newCookie.setMaxAge(24*60*60); // one day     
            //newCookie.setVersion(1);        
            html.h3().content("Cookie not present, new one is created");
            result.addCookie(newCookie);
        } else {
            html.h3().content("Cookie is present, value is: " + a.getValue());
        }
        
        return result;
    }
}
