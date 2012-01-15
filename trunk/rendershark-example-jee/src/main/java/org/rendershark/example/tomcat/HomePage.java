package org.rendershark.example.tomcat;

import static org.rendersnake.HtmlAttributesFactory.action;
import static org.rendersnake.HtmlAttributesFactory.href;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.PageContext;
import org.rendersnake.Renderable;
import org.rendersnake.internal.ContextMap;

@Singleton @Named("/index.html")
public class HomePage implements Renderable {

    @Override
    public void renderOn(HtmlCanvas html) throws IOException {
        ContextMap session = html.getPageContext().getContextMap(PageContext.SESSION);
        
        html.html().body().h1().content("rendershark example page").h3().content("session:"+session)._body()._html();

        String usr = session.getString("usr");
        if (usr == null) {
            html.a(href("login.html")).content("Login");
        } else {
            html.h2().content("Welcome " + usr).a(href("logout.html")).content(" logout");
        }

        html.h3().a(href("static/images/logo.jpeg")).content("Get a static resource")._h3();
        
        html.h3().a(href("doc.xml")).content("Get an XML resource")._h3();
        
        html.h3().a(href("missing.html")).content("Ask for a missing page")._h3();

        html.h3().a(href("forward.html")).content("Forward to login")._h3();
        
        html.h3().a(href("redirect.html")).content("Redirect to login")._h3();
        
        html.h3().a(href("error.html")).content("Throw an exception on GET")._h3();
        
        html.h3()
            .form(action("error.html").method("post"))
                .input(type("submit").value("Throw an exception on POST"))
            ._form()
            ._h3();
    }

}
