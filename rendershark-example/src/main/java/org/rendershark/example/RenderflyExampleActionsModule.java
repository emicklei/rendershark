package org.rendershark.example;

import org.rendershark.core.jmx.JMXAction;
import org.rendershark.example.action.CookieAction;
import org.rendershark.example.action.ErrorAction;
import org.rendershark.example.action.ForwardAction;
import org.rendershark.example.action.HomeAction;
import org.rendershark.example.action.InternalErrorAction;
import org.rendershark.example.action.LoginAction;
import org.rendershark.example.action.LogoutAction;
import org.rendershark.example.action.PageNotFoundAction;
import org.rendershark.example.action.ProductShowAction;
import org.rendershark.example.action.RedirectAction;
import org.rendershark.example.action.XmlDocumentAction;

import com.google.inject.AbstractModule;

public class RenderflyExampleActionsModule  extends AbstractModule {
    
    public void configure() {
        bind(HomeAction.class);
        bind(LoginAction.class);
        bind(LogoutAction.class);
        bind(ErrorAction.class);
        
        bind(PageNotFoundAction.class);
        bind(InternalErrorAction.class);
        bind(ProductShowAction.class);
        
        bind(ForwardAction.class);
        bind(RedirectAction.class);
        
        bind(XmlDocumentAction.class);
        bind(CookieAction.class);
        
        // setup JMX access
        JMXAction jmx = new JMXAction();
        jmx.getControlPage().excludePrefixes.add("java");
        jmx.getControlPage().includePrefixes.add("");
        bind(JMXAction.class).toInstance(jmx);
    }
}
