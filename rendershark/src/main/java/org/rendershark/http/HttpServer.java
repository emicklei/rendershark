package org.rendershark.http;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;


public class HttpServer {
    private static final Logger LOG = LoggerFactory.getLogger(HttpServer.class);
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(getVersion() + "\nrendershark [path/to/rendershark.properties]");
            return;
        }
        HttpServer me = new HttpServer();
        Properties serverProperties = me.createProperties(args[0]);
        if  (serverProperties == null) return;
        
        Options options = new Options(serverProperties);
        if (!options.isValid()) return;
        
        AbstractModule propertiesModule = me.createPropertiesModule(serverProperties); 
        Injector injector = Guice.createInjector(propertiesModule, options.serverModule);        
        me.startListeningOn(injector, options.port);
    }
    
    public Properties createProperties(String location) {
        final Properties serverProperties = new Properties(); 
        try {
            serverProperties.load(new FileInputStream(location));
        } catch (Exception ex) {
            LOG.error("Unable to load properties from;" + location, ex);
            return null;
        }
        return serverProperties;
    }
    
    public AbstractModule createPropertiesModule(String location) {
        return this.createPropertiesModule(this.createProperties(location));
    }
    
    public AbstractModule createPropertiesModule(final Properties serverProperties) {
        if (serverProperties == null) return null;
        // Wrap properties to bind into local Module
        AbstractModule propertiesModule = new AbstractModule() {
            protected void configure() {
               Names.bindProperties(this.binder(),serverProperties); 
               this.bind(Properties.class)
               	.annotatedWith(Names.named(HttpRESTPipelineFactory.PROPERTIES_NAME))
               	.toInstance(serverProperties);
            }
        };
        return propertiesModule;
    }

    
    public void startListeningOn(Injector injector, int port) {
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                		Executors.newCachedThreadPool(),
                		Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        ChannelPipelineFactory factory = injector.getInstance(ChannelPipelineFactory.class);
        bootstrap.setPipelineFactory(factory);

        // Bind and start to accept incoming connections.
        InetSocketAddress localAddress = new InetSocketAddress(port);
        bootstrap.bind(localAddress);

        LOG.info(getVersion() + 
                " is ready for business and listens to " +
                "http://" + localAddress.getHostName() + 
                ":" + port);
    }
    
    
    public static String getVersion() {
        String implVersion = HttpServer.class.getPackage().getImplementationVersion();
        return "rendershark version (" + (implVersion == null ? new Date().toString() : implVersion) + ")";
    }
    
    public static class Options {
    	int port = -1;    	
    	String moduleClassName;
    	Module serverModule;
    	
    	public Options(Properties props) {
    		String value = props.getProperty("http.port", null);
    		if (value != null) {
    			port = Integer.parseInt(value);
    		}
    		moduleClassName = props.getProperty("guice.module");
    	}
    	public boolean isValid() {
    	    if (port == -1) {
    	        LOG.error("Missing [http.port] property");
    	        return false;
    	    }
    	    if (moduleClassName == null) {
                LOG.error("Missing [guice.module] property");
                return false;
            }    		
			try {
				serverModule = (Module)Class.forName(moduleClassName).newInstance();
			} catch (Exception ex) {
				LOG.error("Unable to load module with class name:" + moduleClassName, ex);
				return false;
			}  
			return true;
    	}    	
    }    
}