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
    
    public HandlerResult get(HtmlCanvas html) throws IOException {
        html.render(new JMXLayoutWrapper(control));
        return HandlerResult.ok();
    }

    public HandlerResult post(HtmlCanvas html) throws IOException {
        
        return HandlerResult.redirectTo("/internal/jmx.html");
    }
    
    public JMXControlPage getControlPage() { return control; }
}
