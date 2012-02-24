package org.rendershark.tools;

import java.io.IOException;
import java.io.Writer;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.PageContext;
import org.rendersnake.Renderable;
import org.rendersnake.error.RenderException;
import org.rendersnake.internal.CharactersWriteable;
import org.rendersnake.tools.Inspector;
import org.rendersnake.tools.LoggingPageContext;
/**
 * DebugHtmlCanvas is a special HtmlCanvas that can render inspector wrappers around component on a HTML page.
 * Its purpose is to reveal the structure of a page by displaying debugging information.
 * 
 * @author ernestmicklei
 */
public class DebugHtmlCanvas extends HtmlCanvas {

    private boolean insideBody = false;
	public boolean enabled = true;
	public int renderCount = 0;

	public DebugHtmlCanvas(Writer out) {
		super(out);
	}

	@Override
	public HtmlCanvas render(Renderable component) throws IOException {
		if (null == component) return this;
	    
		// For debugging, we hide the exception component to reveal the offending component
		if (!(component instanceof RenderException))
		    this.getPageContext().withObject(RenderException.KEY_PAGECONTEXT,component);
		
		if (enabled) {
		    renderCount++;
		    if (insideBody) {
		        super.render(new Inspector(component));
		    } else {
		        super.render(component);
		    }
		} else {
			super.render(component);
		}
		return this;
	}

	@Override
	public HtmlCanvas body() throws IOException {
		super.body();
		insideBody = true;
		return this.renderInspectorCss();
	}

	@Override
	public HtmlCanvas body(CharactersWriteable attrs) throws IOException {
		super.body(attrs);
		insideBody = true;
		return this.renderInspectorCss();
	}

	@Override
	public HtmlCanvas _body() throws IOException {
        super._body();
        insideBody = false;
        return this;
	}
	
	private HtmlCanvas renderInspectorCss() throws IOException {
		Inspector.renderCssOn(this);
		return this;
	}
    /**
     * Return a new PageContext for storing component-scoped variables.
     * Create one that logs all set calls.
     * @return a new LoggingPageContext
     */	
	protected PageContext createPageContext() { return new LoggingPageContext(); }
}
