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

import com.google.inject.Injector;
import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;
import com.sun.jersey.server.impl.container.netty.NettyHandlerContainer;

public class HttpRESTPipelineFactory implements ChannelPipelineFactory {
    private static final Logger LOG = LoggerFactory.getLogger(HttpRESTPipelineFactory.class);

    private Injector injector;
    
    @Inject public HttpRESTPipelineFactory(Injector injector) {
        LOG.debug("constructing with:"+injector);
        this.injector = injector;
    }
    
    @Inject @Named("com.sun.jersey.server.impl.container.netty.baseUri") String baseUri;
    @Inject @Named("com.sun.jersey.config.property.classnames") String classNames;
    @Inject @Named("com.sun.jersey.spi.container.ContainerRequestFilters") String filterNames;
    
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
        
        // http://blogs.oracle.com/sandoz/entry/tracing_in_jersey
        props.put("com.sun.jersey.config.feature.TracePerRequest", true); // make props
        props.put("com.sun.jersey.config.feature.Trace",true); // TODO make props
        props.put("com.sun.jersey.spi.container.ContainerRequestFilters", filterNames);
        ResourceConfig rcf = new ClassNamesResourceConfig(props);

        return ContainerFactory.createContainer(
                NettyHandlerContainer.class, 
                rcf, 
                new GuiceComponentProviderFactory(rcf,this.injector));
    }
}
