package org.rendershark.http;

import static org.jboss.netty.channel.Channels.pipeline;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.server.impl.container.netty.NettyHandlerContainer;

public class HttpRESTPipelineFactory implements ChannelPipelineFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HttpRESTPipelineFactory.class);

    @Inject @Named("com.sun.jersey.server.impl.container.netty.baseUri") String baseUri;
    @Inject @Named("com.sun.jersey.config.property.classnames") String classNames;
    
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();
        if (LOG.isDebugEnabled())
            LOG.debug("getPipeline:" + pipeline.hashCode());

        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("jerseyHandler", this.createKerseyHandler());
        return pipeline;
    }

    private NettyHandlerContainer createKerseyHandler() {
        Map<String, Object> props = new HashMap<String, Object>();
        if (classNames == null)
            LOG.warn("Missing property [com.sun.jersey.config.property.classnames]");
        props.put(ClassNamesResourceConfig.PROPERTY_CLASSNAMES, classNames);
        if (baseUri == null)
            LOG.warn("Missing property [com.sun.jersey.server.impl.container.netty.baseUri]");
        props.put(NettyHandlerContainer.PROPERTY_BASE_URI, baseUri);
        ResourceConfig rcf = new ClassNamesResourceConfig(props);

        return ContainerFactory.createContainer(NettyHandlerContainer.class, rcf);
    }
}
