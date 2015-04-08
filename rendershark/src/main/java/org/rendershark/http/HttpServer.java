package org.rendershark.http;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;


public class HttpServer {
    private static final Logger LOG = LoggerFactory.getLogger(HttpServer.class);
    
    private Injector injector;
    private int acceptingPort;
    private Channel acceptingChannel;
    private int numberOfWorkers = Runtime.getRuntime().availableProcessors() * 2;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println(getVersion() + "\nrendershark [path/to/rendershark.properties]");
            return;
        }
        HttpServer me = new HttpServer();
        me.init(args);
        me.startUp();
    }

    public void setNumberOfWorkers(int numberOfWorkers) {
        this.numberOfWorkers=numberOfWorkers;
    }

    public void init(String[] args) {
        Properties serverProperties = this.loadProperties(args[0]);
        if  (serverProperties == null) return;
        
        Options options = new Options(serverProperties);
        if (!options.isValid()) return;
        
        AbstractModule propertiesModule = createPropertiesModule(serverProperties); 
        this.init(Guice.createInjector(propertiesModule, options.serverModule),options.port);
    }
    
    public void init(Injector injector, int listeningPort) {
        this.injector = injector;
        this.acceptingPort = listeningPort;
    }
    
    public Properties loadProperties(String location) {
        final Properties serverProperties = new Properties(); 
        try {
            serverProperties.load(new FileInputStream(location));
        } catch (Exception ex) {
            LOG.error("Unable to load properties from;" + location, ex);
            return null;
        }
        return serverProperties;
    }
    // TODO move this to another
    public static AbstractModule createPropertiesModule(final Properties serverProperties) {
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
 
    public boolean isStarted() { return acceptingChannel != null && acceptingChannel.isBound(); }
    
    public boolean startUp() {
        if (acceptingChannel != null) {
            LOG.warn("Server was not (yet) properly shutdown.");
            return false;
        }
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                		Executors.newCachedThreadPool(),
                		Executors.newCachedThreadPool(),
                        numberOfWorkers));

        // Set up the event pipeline factory.
        ChannelPipelineFactory factory = injector.getInstance(ChannelPipelineFactory.class);
        bootstrap.setPipelineFactory(factory);

        // Bind and start to accept incoming connections.
        InetSocketAddress localAddress = new InetSocketAddress(this.acceptingPort);
        this.acceptingChannel = bootstrap.bind(localAddress);

        if (acceptingChannel.isOpen()) {
            LOG.info(getVersion() + 
                " is ready for business and listens to " +
                "http://" + localAddress.getHostName() + 
                ":" + this.acceptingPort);
            return true;
        } else {
            LOG.error("Unable to start listing on " +
                "http://" + localAddress.getHostName() + 
                ":" + this.acceptingPort);
            return false;
        }                
    }    
    public void shutDown() {
        if (acceptingChannel == null) {
            LOG.warn("Server was already shutdown.");
            return;
        }        
        LOG.info("Shutting down the accept channel ; stop listening");
        ChannelFuture future = acceptingChannel.close();
        try {
            future.await();
        } catch (InterruptedException e) {
            LOG.error("Interrupt exception caught while waiting for shutdown to complete.");
        }
        this.acceptingChannel = null;
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
