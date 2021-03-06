package org.rendershark.example.action;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.rendershark.core.HandlerResult;
import org.rendershark.core.HttpGetHandler;
import org.rendershark.example.page.HomePage;
import org.rendersnake.HtmlCanvas;

@Singleton @Named("/index.html")
public class HomeAction implements HttpGetHandler {

    public void get(HtmlCanvas html, HandlerResult result) throws IOException {
        html.render(new HomePage());
    }   
}
