package org.rendershark.https;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.ssl.SslHandler;

public class MagicDetectionSslHandler extends SslHandler {

    public MagicDetectionSslHandler(SSLEngine engine) {
        super(engine);
    }
    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        // Will use the first two bytes to detect a protocol.
        if (buffer.readableBytes() < 2) {
            return null;
        }
        final int magic1 = buffer.getUnsignedByte(buffer.readerIndex());

        if (isSsl(magic1)) {
            return super.decode(ctx, channel, buffer);
        } else {
            // Unknown protocol; discard everything and close the connection.
            buffer.skipBytes(buffer.readableBytes());
            ctx.getChannel().close();
            return null;
        }
    }
    private boolean isSsl(int magic1) {
        switch (magic1) {
        case 20: case 21: case 22: case 23: case 255:
            return true;
        default:
            return magic1 >= 128;
        }
    }        
}
