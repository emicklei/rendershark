package org.rendershark.example.action;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.rendershark.core.HandlerResult;
import org.rendershark.core.HttpGetHandler;
import org.rendersnake.HtmlCanvas;

@Singleton @Named("/redirect.html")
public class RedirectAction implements HttpGetHandler {

    public void get(HtmlCanvas html,HandlerResult result) throws IOException {
        result.redirectTo("/login.html");
    }
}
