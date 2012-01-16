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

    public void get(HtmlCanvas html, HandlerResult result) throws IOException {
        html.render(new LoginPage());
    }    
    
    public void post(HtmlCanvas html,HandlerResult result) throws IOException {
        String usr = html.getRequestParameters().getString("user");
        ContextMap session = html.getSession();
        session.withString("usr", usr);
        result.redirectTo("/index.html");
    }    
}
