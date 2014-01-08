package org.rendershark.core;

import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseDecoder;
import org.rendershark.core.error.DefaultErrorAction;
import org.rendershark.core.logging.LoggerManager;
import org.rendershark.http.HttpRESTPipelineFactory;
import org.rendershark.http.HttpRequestHandler;
import org.rendershark.http.HttpSelectiveContentCompressor;
import org.rendershark.http.HttpServerPipelineFactory;
import org.rendershark.http.HttpStaticFileServerHandler;
import org.rendershark.http.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

public abstract class RendersharkModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger(RendersharkModule.class);
    
    @Override
    protected void configure() {
        bind(HttpRequestDecoder.class);
        bind(HttpResponseDecoder.class);        
        bind(LoggerManager.class).toInstance(new LoggerManager());
    }
    
    public static class REST extends RendersharkModule {

        @Override
        protected void configure() {
            super.configure();
            LOG.info("Configure for REST processing");            
            bind(ChannelPipelineFactory.class).to(HttpRESTPipelineFactory.class);
        }
    }
    public static class HTML extends RendersharkModule {

        @Override
        protected void configure() {
            super.configure();
            LOG.info("Configure for HTML rendering");
            bind(ChannelPipelineFactory.class).to(HttpServerPipelineFactory.class);
            bind(HttpStaticFileServerHandler.class).in(com.google.inject.Singleton.class);
            bind(HttpSelectiveContentCompressor.class);
            bind(HttpRequestHandler.class);
            bind(SessionManager.class);        
            bind(Dispatcher.class).toInstance(new Dispatcher(new DefaultErrorAction()));
        }
    }  
}
