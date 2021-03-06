package org.rendershark.http;

import static org.jboss.netty.channel.Channels.pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;
import com.sun.jersey.server.impl.container.netty.NettyHandlerContainer;
/**
 * 
 * For tracing properties: http://blogs.oracle.com/sandoz/entry/tracing_in_jersey
 * @author emicklei
 *
 */
public class HttpRESTPipelineFactory implements ChannelPipelineFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HttpRESTPipelineFactory.class);
    public static final String PROPERTIES_NAME = "resourceconfig.properties";
    
    private Injector injector;
    private NettyHandlerContainer container;
    private int maxChunkedContentLength = 64*1024; //TODO should become a externalized property
    
    @Inject public HttpRESTPipelineFactory(Injector injector) {
        this.injector = injector;
    }
    @Inject @Named(PROPERTIES_NAME) Properties properties;
    
    /**
     * It is assumed the NettyHandlerContainer can be a singleton for all pipelines.
     */
    @Inject
    void prepareJerseyHandler() {
        this.container = this.createJerseyHandler();
    }
    
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("chunky", new HttpChunkAggregator(maxChunkedContentLength));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("jerseyHandler", this.container);
        return pipeline;
    }

    private NettyHandlerContainer createJerseyHandler() {
        Map<String, Object> props = new HashMap<String, Object>();
        for (Object key : properties.keySet()) {
        	props.put((String)key, properties.get(key));
        	LOG.debug(key+"="+properties.get(key));
        }        
        if (props.get(ClassNamesResourceConfig.PROPERTY_CLASSNAMES) == null)
            LOG.warn("Missing property [com.sun.jersey.config.property.classnames]");
        if (props.get(NettyHandlerContainer.PROPERTY_BASE_URI) == null)
            LOG.warn("Missing property [com.sun.jersey.server.impl.container.netty.baseUri]");

        ResourceConfig rcf = new ClassNamesResourceConfig(props);

        return ContainerFactory.createContainer(
                NettyHandlerContainer.class, 
                rcf, 
                new GuiceComponentProviderFactory(rcf,this.injector));
    }

    public void setMaxChunkedContentLength(int maxChunkedContentLength) {
        this.maxChunkedContentLength = maxChunkedContentLength;
    }
}
