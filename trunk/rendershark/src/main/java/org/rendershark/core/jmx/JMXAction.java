package org.rendershark.core.jmx;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.rendershark.core.HandlerResult;
import org.rendershark.core.HttpGetHandler;
import org.rendershark.core.HttpPostHandler;
import org.rendersnake.HtmlCanvas;

@Singleton
@Named("/internal/jmx.html")
public class JMXAction implements HttpGetHandler , HttpPostHandler {

    JMXControlPage control = new JMXControlPage();
    
    public void get(HtmlCanvas html, HandlerResult result) throws IOException {
        html.render(new JMXLayoutWrapper(control));
    }

    public void post(HtmlCanvas html, HandlerResult result) throws IOException {        
        result.redirectTo("/internal/jmx.html");
    }
    
    public JMXControlPage getControlPage() { return control; }
}
