# http engine for rendersnake components #

Rendershark uses the following libraries/frameworks:
  * JBoss Netty
  * Cgbystrom Netty-Jersey bridge
  * Google Guava Caching
  * Google Guice
  * SLF4J
  * renderSnake

Rendershark dispatches Http requests to objects. Mappings are specified by annotating its class.
GET requests are handled by an implementor of HttpGetHandler, typically some Action class.

```
@Singleton @Named("/index.html")
public class HomeAction implements HttpGetHandler {

    public void get(HtmlCanvas html, HandlerResult result) throws IOException {
        html.render(new HomePage());
    }   
}
```

POST requests are handled by an implementor of HttpPostHandler.

```
@Singleton @Named("/login.html")
public class LoginAction implements HttpGetHandler, HttpPostHandler {

    public void get(HtmlCanvas html, HandlerResult result) throws IOException {
        html.render(new LoginPage());
    }    
    
    public void post(HtmlCanvas html,HandlerResult result) throws IOException {
        String usr = html.getRequestParameters().getString("user");
        ContextMap session = html.getSession();
        session.withString("usr", usr);
        result.redirectTo("/index.html");
    }    
}
```

Rendershark uses renderSnake for HTML rendering

```
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
```

Rendershark uses Google Guice for dependency injection.

```
public class RendersharkExampleActionsModule  extends AbstractModule {
    
    public void configure() {
        bind(HomeAction.class);
        bind(LoginAction.class);
        bind(LogoutAction.class);
    }
}
```

Rendershark uses simple properties to specify port, module etc

```
public class RendersharkExampleModule extends AbstractModule {
    public static void main(String[] args) {
        HttpServer.main(new String[]{"./src/main/resources/rendershark.properties"});
    }
    
    public void configure() {
        install(new RendersharkModule.HTML());
        install(new RendersharkExampleActionsModule());
    }
}
```

Noticed the main method in this class? That's all you need to run the engine with your app.

Example rendersnake.properties
```
guice.module=com.philemonworks.simon.ApplicationModule
http.port=8181
com.sun.jersey.config.property.classnames=com.philemonworks.simon.rest.StatsProviderResource
static.local.path=package
static.uri.prefix=/static
```
