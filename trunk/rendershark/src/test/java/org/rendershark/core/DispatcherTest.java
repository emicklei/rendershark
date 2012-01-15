package org.rendershark.core;

import java.io.IOException;
import java.net.URI;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rendershark.core.error.DefaultErrorAction;
import org.rendersnake.HtmlCanvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatcherTest {
    private static final Logger LOG = LoggerFactory.getLogger(Dispatcher.class);
    
    Dispatcher dispatcher;
    HtmlCanvas canvas;
    
    @Before
    public void setUp() {
        dispatcher = new Dispatcher(new DefaultErrorAction());
        canvas = new HtmlCanvas();
    }
    
    @Test
    public void testGet() throws Exception {
        this.addGetHandlerTo(dispatcher, "/get");
        Assert.assertTrue(dispatcher.handleGet(new URI("/get"), canvas).isHandled);
        Assert.assertTrue(canvas.getPageContext().getBoolean("get.handled"));
    }
    
    private void addGetHandlerTo(Dispatcher dispatcher, String uri) {
        dispatcher.register(new HttpGetHandler() {
            public HandlerResult get(HtmlCanvas html) throws IOException {
                LOG.debug("handling get");
                html.getPageContext().withObject("get.handled", true);
                return HandlerResult.ok();
            }
        }, uri);
    }
    private void addPostHandlerTo(Dispatcher dispatcher, String uri) {
        dispatcher.register(new HttpPostHandler() {
            public HandlerResult post(HtmlCanvas html) throws IOException {
                LOG.debug("handling post");
                html.getPageContext().withObject("post.handled", true);
                return HandlerResult.ok();
            }
        }, uri);
    }
    private void addErrorGetHandlerTo(Dispatcher dispatcher, String uri) {
        dispatcher.register(new HttpGetHandler() {
            public HandlerResult get(HtmlCanvas html) throws IOException {
                LOG.debug("handling error");
                html.getPageContext().withObject("error.handled", true);
                return HandlerResult.ok();
            }
        }, uri);
    }
}
