package org.rendershark.example.action;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.rendershark.core.HandlerResult;
import org.rendershark.core.HttpGetHandler;
import org.rendershark.core.HttpPostHandler;
import org.rendershark.example.page.LoginPage;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.internal.ContextMap;

@Singleton @Named("/login.html")
public class LoginAction implements HttpGetHandler, HttpPostHandler {

    public HandlerResult get(HtmlCanvas html) throws IOException {
        html.render(new LoginPage());
        return HandlerResult.ok();
    }    
    
    public HandlerResult post(HtmlCanvas html) throws IOException {
        String usr = html.getRequestParameters().getString("user");
        ContextMap session = html.getSession();
        session.withString("usr", usr);
        return HandlerResult.redirectTo("/index.html");
    }    
}
