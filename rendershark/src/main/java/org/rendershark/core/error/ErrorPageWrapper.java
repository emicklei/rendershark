package org.rendershark.core.error;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;
import org.rendersnake.RenderableWrapper;
import org.rendersnake.ext.jquery.JQueryLibrary;

public class ErrorPageWrapper extends RenderableWrapper {

    public ErrorPageWrapper(Renderable component) {
        super(component);
    }
    
    
    @Override
    public void renderOn(HtmlCanvas html) throws IOException {
       html
           .html()
               .head()
                   .render(JQueryLibrary.core("1.6.4"))
                   .render(JQueryLibrary.theme("1.8.16","base"))
                   .render(JQueryLibrary.ui("1.8.16"))                   
               ._head()
               .body()
                   .render(this.component)
               ._body()
           ._html();
    }

}
