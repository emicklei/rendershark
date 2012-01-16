package org.rendershark.core;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

public interface HttpPostHandler {
    void post(HtmlCanvas html, HandlerResult result) throws IOException;
}
