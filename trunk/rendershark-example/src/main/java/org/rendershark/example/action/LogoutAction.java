package org.rendershark.example.action;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.rendershark.core.HandlerResult;
import org.rendershark.core.HttpGetHandler;
import org.rendershark.http.session.HttpSession;
import org.rendersnake.HtmlCanvas;

@Singleton @Named("/logout.html")
public class LogoutAction implements HttpGetHandler {

    public HandlerResult get(HtmlCanvas html) throws IOException {
        ((HttpSession)html.getSession()).invalidate();
        return HandlerResult.redirectTo("/index.html");
    }        
}
