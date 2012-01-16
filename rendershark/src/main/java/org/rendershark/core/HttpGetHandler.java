package org.rendershark.core;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;

public interface HttpGetHandler {
    void get(HtmlCanvas html, HandlerResult result) throws IOException;
}
