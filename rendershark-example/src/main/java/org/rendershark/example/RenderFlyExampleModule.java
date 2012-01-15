package org.rendershark.example;

import org.rendershark.core.RenderflyModule;
import org.rendershark.http.HttpServer;

import com.google.inject.AbstractModule;

public class RenderFlyExampleModule extends AbstractModule {
    public static void main(String[] args) {
        HttpServer.main(new String[]{"./src/main/resources/rendershark.properties"});
    }
    
    public void configure() {
        install(new RenderflyModule.HTML());
        install(new RenderflyExampleActionsModule());
    }
}
