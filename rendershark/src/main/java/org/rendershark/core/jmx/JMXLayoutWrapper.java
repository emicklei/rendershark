package org.rendershark.core.jmx;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;
import org.rendersnake.RenderableWrapper;
import org.rendersnake.ext.jquery.JQueryLibrary;

public class JMXLayoutWrapper extends RenderableWrapper {

    public JMXLayoutWrapper(Renderable component) {
        super(component);
    }

    @Override
    public void renderOn(HtmlCanvas html) throws IOException {
       html
           .html()
               .head()
                   .render(JQueryLibrary.core("1.6.4"))
                   .render(JQueryLibrary.theme("1.8.16","redmond"))
                   .render(JQueryLibrary.ui("1.8.16"))                   
               ._head()
               .body()
                   .render(this.component)
               ._body()
           ._html();
    }
}
