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
        dispatcher.handleGet(new URI("/get"), canvas);
        Assert.assertTrue(canvas.getPageContext().getBoolean("get.handled"));
    }
    
    private void addGetHandlerTo(Dispatcher dispatcher, String uri) {
        dispatcher.register(new HttpGetHandler() {
            public void get(HtmlCanvas html, HandlerResult result) throws IOException {
                LOG.debug("handling get");
                html.getPageContext().withObject("get.handled", true);
            }
        }, uri);
    }
    private void addPostHandlerTo(Dispatcher dispatcher, String uri) {
        dispatcher.register(new HttpPostHandler() {
            public void post(HtmlCanvas html,HandlerResult result) throws IOException {
                LOG.debug("handling post");
                html.getPageContext().withObject("post.handled", true);
            }
        }, uri);
    }
    private void addErrorGetHandlerTo(Dispatcher dispatcher, String uri) {
        dispatcher.register(new HttpGetHandler() {
            public void get(HtmlCanvas html, HandlerResult result) throws IOException {
                LOG.debug("handling error");
                html.getPageContext().withObject("error.handled", true);
            }
        }, uri);
    }
}
