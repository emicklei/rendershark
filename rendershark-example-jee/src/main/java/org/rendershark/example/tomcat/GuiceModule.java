package org.rendershark.example.tomcat;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HomePage.class);
        install(new ServletModule() {

            @Override
            protected void configureServlets() {
                serve("/web/*").with(org.rendersnake.ext.guice.GuiceComponentServlet.class);
            }
        });
    }
}
