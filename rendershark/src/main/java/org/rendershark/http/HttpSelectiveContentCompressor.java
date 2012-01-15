package org.rendershark.http;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSelectiveContentCompressor extends HttpContentCompressor {
    private static final Logger LOG = LoggerFactory.getLogger(HttpSelectiveContentCompressor.class);
    
    boolean skipCompression = false;
    
    public String extensionToSkip = "png.jpg.gif.jpeg.ico.zip";
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        Object msg = e.getMessage();
        if (!(msg instanceof HttpRequest)) {
            ctx.sendUpstream(e);
            return;
        }    
        HttpRequest request =  (HttpRequest)msg;
        String uri = request.getUri();
        int dot = uri.lastIndexOf('.');
        String extension = uri.substring(dot+1);
        skipCompression = extensionToSkip.indexOf(extension) != -1;
        if (LOG.isTraceEnabled()) {
            LOG.trace("message received:" + uri + " ,skip compress=" + skipCompression);
        }
        if (skipCompression) {
            ctx.sendUpstream(e);
        } else { 
            super.messageReceived(ctx, e);
        }
    }
    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        if (LOG.isTraceEnabled()) {
            LOG.trace("write request skip compress=" + skipCompression);
        }
        if (skipCompression) {
            ctx.sendDownstream(e);
        } else {
            super.writeRequested(ctx, e);
        }
    }
}
