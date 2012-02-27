package org.rendershark.http;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultCookie;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;
import org.rendershark.core.Dispatcher;
import org.rendershark.core.HandlerResult;
import org.rendershark.core.HttpHeader;
import org.rendershark.core.error.ErrorConstants;
import org.rendershark.http.session.HttpSession;
import org.rendershark.http.session.SessionManager;
import org.rendershark.tools.DebugHtmlCanvas;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.PageContext;
import org.rendersnake.internal.ContextMap;
import org.rendersnake.internal.SimpleContextMap;
import org.rendersnake.internal.WriteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpRequestHandler is the end of the pipeline and is responsible for dispatching
 * the GET, POST and HEAD request to the registered handler.
 * Before dispatching to the handler a fresh new HtmlCanvas is prepared.
 * This HtmlCanvas contains:
 * <ul>
 * <li> access to the HTTP headers </li> 
 * <li> access to the HTTP session </li> 
 * <li> access to the HTTP cookies </li> 
 * <li> access to the HTTP request parameters </li> 
 * </ul> 
 * https://docs.jboss.org/author/display/ISPN/Getting+Started+Guide
 * 
 * NOTE:  Per Thread one Handler
 * @author ernestmicklei
 */
public class HttpRequestHandler extends SimpleChannelUpstreamHandler {
    private static final String COOKIE_NAME_JSESSIONID = "JSESSIONID";
    private static final HttpSession NO_SESSION = null;
    private static final ContextMap READONLY_EMPTY_MAP = new SimpleContextMap(); 
    
    private static final Logger LOG = LoggerFactory.getLogger(HttpRequestHandler.class);

    @Inject
    SessionManager sessionManager;
    @Inject
    Dispatcher dispatcher;

    private final WriteBuffer buffer = new WriteBuffer(4096);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
        final HttpRequest request = (HttpRequest) event.getMessage();        
        buffer.reset();
        final URI uri = URI.create(request.getUri());
        HtmlCanvas canvas = new HtmlCanvas(buffer);
        // TODO rethink this
        if (LOG.isDebugEnabled() && "inspect".equals(uri.getQuery())) {
            canvas = new DebugHtmlCanvas(buffer);
        }
        final HttpMethod httpMethod = request.getMethod();

        final PageContext context = canvas.getPageContext();
        this.shareHeadersInto(request, context);
        this.shareCookiesInto(request, context);
        this.shareQueryParametersInto(uri.getRawQuery(), context);
        final HttpSession session = this.shareSessionInto(request, context);

        if (HttpMethod.GET == httpMethod) {
            this.handleGet(event, uri, session, canvas);
        } else if (HttpMethod.POST == httpMethod) {
            this.handlePost(event, request, uri, session, canvas, context);
        } else if (HttpMethod.HEAD == httpMethod) {
            this.handleHead(event, uri, canvas);
        } else {
            this.handleMethodNotImplemented(uri, event, canvas, NO_SESSION);
        }
    }
    
    private void shareHeadersInto(HttpRequest request, PageContext context) {
        context.withObject(PageContext.REQUEST_HEADERS, new HttpHeaderReadAccess(request));
    }

    private void shareCookiesInto(HttpRequest request, PageContext context) {
        context.withObject(PageContext.REQUEST_COOKIES, new HttpCookiesReadAccess(request));
    }    
    
    private void handleHead(MessageEvent event, URI uri, HtmlCanvas canvas) {
        if (this.dispatcher.handleHead(uri)) {
            this.writeResponse(event, NO_SESSION, HttpResponseStatus.OK, null);
        } else {
            this.writeResponse(event, NO_SESSION, HttpResponseStatus.NOT_FOUND, null);
        }
    }

    private void handleMethodNotImplemented(URI uri, MessageEvent event, HtmlCanvas canvas, HttpSession session) throws IOException {
        this.dispatcher.handleErrorStatus(uri, HttpResponseStatus.NOT_IMPLEMENTED, canvas);
        this.writeResponse(event, session, HttpResponseStatus.NOT_IMPLEMENTED, null);
    }

    private void handlePost(MessageEvent event, HttpRequest request, URI uri, HttpSession session, HtmlCanvas canvas, PageContext context) throws Exception {
        ChannelBuffer content = request.getContent();
        if (content.readable()) {
            String postBody = content.toString(CharsetUtil.UTF_8);
            this.shareBodyParametersInto(postBody, context);
        }
        HttpResponseStatus status = HttpResponseStatus.OK;
        HandlerResult result = null;
        try {
            result = this.dispatcher.handlePost(uri, canvas);
            if (result.isForward()) {
                this.handlePost(event, request, new URI(result.forwardUrl), session, canvas, context);
                return;
            } else if (result.isRedirect()) {
                this.sendRedirectTo(event, result.redirectUrl);
                return;
            }
        } catch (Exception ex) {
            LOG.trace("Failed to handle POST " + uri, ex);
            canvas.getPageContext().withObject(ErrorConstants.CONTEXT_EXCEPTION, ex);
            this.dispatcher.handleErrorStatus(uri, HttpResponseStatus.INTERNAL_SERVER_ERROR, canvas);
            status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
        }
        if (result != null && !result.isHandled) {
            this.dispatcher.handleErrorStatus(uri, HttpResponseStatus.NOT_FOUND, canvas);
            status = HttpResponseStatus.NOT_FOUND;
        }
        this.writeResponse(event, null, status, result);
    }

    private void handleGet(MessageEvent event, URI uri, HttpSession session, HtmlCanvas canvas) throws Exception {
        HttpResponseStatus status = HttpResponseStatus.OK;
        HandlerResult result = null;
        try {
            result = this.dispatcher.handleGet(uri, canvas);
            if (result.isForward()) {
                this.handleGet(event, new URI(result.forwardUrl), session, canvas);
                return;
            } else if (result.isRedirect()) {
                this.sendRedirectTo(event, result.redirectUrl);
                return;
            }
        } catch (Exception ex) {
            LOG.trace("Failed to handle GET " + uri, ex);
            canvas.getPageContext().withObject(ErrorConstants.CONTEXT_EXCEPTION, ex);
            this.dispatcher.handleErrorStatus(uri, HttpResponseStatus.INTERNAL_SERVER_ERROR, canvas);
            status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
            result = new HandlerResult();
        }
        if (!result.isHandled) {
            this.dispatcher.handleErrorStatus(uri, HttpResponseStatus.NOT_FOUND, canvas);
            status = HttpResponseStatus.NOT_FOUND;
        }
        this.writeResponse(event, session, status, result);
    }

    private void sendRedirectTo(MessageEvent e, String redirectUrl) {
        HttpRequest request = (HttpRequest) e.getMessage();
        boolean keepAlive = isKeepAlive(request);
        
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.FOUND);
        response.setHeader(HttpHeaders.Names.LOCATION, redirectUrl);
        if (keepAlive) response.setHeader(CONTENT_LENGTH,0);
        // Write the response.
        ChannelFuture future = e.getChannel().write(response);

        // Close the non-keep-alive connection after the write operation is done.
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private HttpSession shareSessionInto(HttpRequest request, PageContext context) {
        String sessionID = null;
        String cookieString = request.getHeader(COOKIE);
        if (cookieString != null) {
            Set<Cookie> cookies = new CookieDecoder().decode(cookieString);
            sessionID = this.getSessionID(cookies);
        }
        HttpSession session = sessionManager.getSession(sessionID, true);
        context.withObject(PageContext.SESSION, session);
        return session;
    }

    private String getSessionID(Set<Cookie> cookies) {
        for (Cookie each : cookies) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Cookie IN:" + each.toString());
            }
            // secure cookies are different
            if (COOKIE_NAME_JSESSIONID.equals(each.getName()))
                return each.getValue();
        }
        return null;
    }

    private void writeResponse(MessageEvent e, HttpSession sessionOrNull, HttpResponseStatus status, HandlerResult handlerResultOrNull) {
        HttpRequest request = (HttpRequest) e.getMessage();
        boolean keepAlive = isKeepAlive(request);
        
        // Build the response object.
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.setContent(ChannelBuffers.copiedBuffer(buffer.toString(), CharsetUtil.UTF_8));  // TODO now entire page is in buffer before copy ; optimize this
        if (handlerResultOrNull != null && handlerResultOrNull.hasHeaders()) {
            for (HttpHeader each : handlerResultOrNull.getHeaders()) {
                response.setHeader(each.name,each.value);
            }
        } else {
            response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
        }

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
        }

        if (OK == status) { // no cookie if failure?
            // Encode the cookie.
            Set<Cookie> cookies = new HashSet<Cookie>();
            // take request cookies
            String cookieString = request.getHeader(COOKIE);
            if (cookieString != null) {
                CookieDecoder cookieDecoder = new CookieDecoder();
                cookies.addAll(cookieDecoder.decode(cookieString));
            }            
            // ensure session cookie
            if (sessionOrNull != null)
                this.ensureSessionCookie(cookies, sessionOrNull);
            // action specfic cookies
            if (handlerResultOrNull != null && handlerResultOrNull.hasCookies()) {
                cookies.addAll(handlerResultOrNull.cookies);
            }
            if (!cookies.isEmpty()) {
                for (Cookie cookie : cookies) {
                    CookieEncoder cookieEncoder = new CookieEncoder(true);
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Cookie OUT:" + cookie.toString());
                    }
                    cookieEncoder.addCookie(cookie);
                    response.addHeader(SET_COOKIE, cookieEncoder.encode());
                }                
            }
        } else {
            LOG.trace(status.toString());
        }
        // Write the response.
        ChannelFuture future = e.getChannel().write(response);

        // Close the non-keep-alive connection after the write operation is done.
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void ensureSessionCookie(Set<Cookie> cookies, HttpSession session) {
        // try overwrite existing or empty out
        for (Cookie each : cookies) {
            if (each.getName().equals(COOKIE_NAME_JSESSIONID)) {
                if (!session.isValid()) {   
                    each.setValue("");
                } else {
                    each.setValue(session.getId());
                }
                return;
            }
        }
        // new cookie
        Cookie cookie = new DefaultCookie(COOKIE_NAME_JSESSIONID, session.getId());
        cookie.setSecure(false);
        cookie.setPath("/");
        cookies.add(cookie);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }

    void shareBodyParametersInto(String body, PageContext context) {
        // until something better
        this.shareQueryParametersInto("?"+body, context);
    }

    void shareQueryParametersInto(String query, PageContext context) {
        ContextMap parametersMap = (ContextMap) context.getObject(PageContext.REQUEST_PARAMETERS, new SimpleContextMap());
        context.withObject(PageContext.REQUEST_PARAMETERS, parametersMap);
        
        if (query == null) {
            return;
        }
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(query, CharsetUtil.UTF_8);
        Map<String, List<String>> params = queryStringDecoder.getParameters();        
        if (!params.isEmpty()) {
            for (Entry<String, List<String>> p : params.entrySet()) {
                String key = p.getKey();
                List<String> vals = p.getValue();
                // simplify value if only one
                if (vals.size() == 1) {
                    parametersMap.withObject(key, vals.get(0));
                } else if (vals.size() > 1) {
                    parametersMap.withObject(key, vals);
                }
            }
        }
    }
}
