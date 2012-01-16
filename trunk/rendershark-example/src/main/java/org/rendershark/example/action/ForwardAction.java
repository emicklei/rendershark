package org.rendershark.example.action;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.rendershark.core.HandlerResult;
import org.rendershark.core.HttpGetHandler;
import org.rendersnake.HtmlCanvas;

@Singleton @Named("/forward.html")
public class ForwardAction implements HttpGetHandler {

    public void get(HtmlCanvas html, HandlerResult result) throws IOException {
        result.forwardTo("/login.html");
    }
}
