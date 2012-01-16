package org.rendershark.example.action;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.rendershark.core.HandlerResult;
import org.rendershark.core.HttpGetHandler;
import org.rendersnake.HtmlCanvas;

@Singleton @Named("/404.html")
public class PageNotFoundAction implements HttpGetHandler {

	public void get(HtmlCanvas html,HandlerResult result) throws IOException {
		html.html().body()
		    .h1().content("Page not found (you can create your own 404)")
		    ._body()._html();
	}
}
