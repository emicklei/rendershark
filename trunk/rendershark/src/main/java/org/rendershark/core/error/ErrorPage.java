package org.rendershark.core.error;

import static org.rendersnake.HtmlAttributesFactory.class_;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;

public class ErrorPage implements Renderable {

    public void renderOn(HtmlCanvas html) throws IOException {
        html.h1(class_("ui-corner-all")).content(
                html.getPageContext().getObject(ErrorConstants.CONTEXT_STATUS,"{missing context status}").toString());
        
        html.h4().content(
                html.getPageContext().getObject(ErrorConstants.CONTEXT_URI, "{missing context uri}").toString());
        
        Exception ex = (Exception)html.getPageContext().getObject(ErrorConstants.CONTEXT_EXCEPTION);
        if (ex != null) {
            for (StackTraceElement each : ex.getStackTrace()) {
                html.write(each.toString()).br();
            }
        }
    }
}
