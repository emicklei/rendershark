package org.rendershark.example;

import org.rendershark.core.RendersharkModule;
import org.rendershark.http.HttpServer;

import com.google.inject.AbstractModule;

public class RendersharkExampleModule extends AbstractModule {
    public static void main(String[] args) {
        HttpServer.main(new String[]{"./src/main/resources/rendershark.properties"});
    }
    
    public void configure() {
        install(new RendersharkModule.HTML());
        install(new RendersharkExampleActionsModule());
    }
}
