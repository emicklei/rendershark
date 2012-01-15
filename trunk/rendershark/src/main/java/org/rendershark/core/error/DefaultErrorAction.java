package org.rendershark.core.error;

import java.io.IOException;

import org.rendershark.core.HandlerResult;
import org.rendershark.core.HttpGetHandler;
import org.rendersnake.HtmlCanvas;

public class DefaultErrorAction implements HttpGetHandler {
    
    public HandlerResult get(HtmlCanvas html) throws IOException {
        html.render(new ErrorPageWrapper(new ErrorPage()));
        return HandlerResult.ok();
    }
}
