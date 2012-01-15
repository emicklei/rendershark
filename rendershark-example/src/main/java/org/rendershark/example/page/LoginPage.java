package org.rendershark.example.page;

import static org.rendersnake.HtmlAttributesFactory.method;
import static org.rendersnake.HtmlAttributesFactory.type;

import java.io.IOException;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;

public class LoginPage implements Renderable {

    public void renderOn(HtmlCanvas html) throws IOException {
        
        html.html().body();
        html.h1().content("Login Form");
        
        html.form(method("post").action("login.html?country=nl"))
            .input(type("text").name("user"))
            .input(type("password").name("password"))     
            .input(type("submit").value("Try"))
            ._form();

        html._body()._html();
    }

}
