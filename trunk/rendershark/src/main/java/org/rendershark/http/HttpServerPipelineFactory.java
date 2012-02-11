package org.rendershark.http;

import static org.jboss.netty.channel.Channels.pipeline;

import javax.inject.Inject;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;

/**
 * @author emicklei
 */
public class HttpServerPipelineFactory implements ChannelPipelineFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HttpServerPipelineFactory.class);
    
    @Inject Provider<HttpRequestDecoder> requestDecoderProvider;
    @Inject Provider<HttpResponseEncoder> responseEncoderProvider;
    @Inject Provider<HttpSelectiveContentCompressor> contentCompressorProvider;
    @Inject Provider<HttpRequestHandler> requestHandlerProvider;
    @Inject Provider<HttpStaticFileServerHandler> staticHandlerProvider;
    
    public ChannelPipeline getPipeline() throws Exception {
        LOG.trace("new pipeline");
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("decoder", requestDecoderProvider.get());
        pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
        pipeline.addLast("encoder", responseEncoderProvider.get());
        pipeline.addLast("deflater", contentCompressorProvider.get());
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        pipeline.addLast("static", staticHandlerProvider.get());
        pipeline.addLast("handler", requestHandlerProvider.get());
        return pipeline;
    }
}
