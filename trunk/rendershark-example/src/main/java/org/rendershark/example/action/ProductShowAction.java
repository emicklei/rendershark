package org.rendershark.example.action;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.rendershark.core.HandlerResult;
import org.rendershark.core.HttpGetHandler;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.PageContext;

@Singleton @Named("/product/{id}")
public class ProductShowAction implements HttpGetHandler {

    public void get(HtmlCanvas html,HandlerResult result) throws IOException {
        String id = html.getPageContext().getContextMap(PageContext.REQUEST_PATH).getString("id");
        html.html().body().h2().content(id)._body()._html();
    }   
}
