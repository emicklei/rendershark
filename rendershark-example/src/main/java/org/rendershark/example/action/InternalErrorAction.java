package org.rendershark.example.action;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.rendershark.core.HandlerResult;
import org.rendershark.core.HttpGetHandler;
import org.rendershark.core.error.ErrorConstants;
import org.rendersnake.HtmlCanvas;

@Singleton @Named("/500.html")
public class InternalErrorAction implements HttpGetHandler {

	public HandlerResult get(HtmlCanvas html) throws IOException {
		Exception ex = (Exception)html.getPageContext().getObject(ErrorConstants.CONTEXT_EXCEPTION);
		html.html().body().h1().content("Internal Error (you can create your own 500)");
		
		for (StackTraceElement each : ex.getStackTrace()) {
			html.write(each.toString()).br();
		}
		html._body()._html();
		
		return HandlerResult.ok();
	}
}
