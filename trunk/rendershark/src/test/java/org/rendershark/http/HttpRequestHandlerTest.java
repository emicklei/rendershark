package org.rendershark.http;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.rendershark.http.HttpRequestHandler;
import org.rendersnake.PageContext;
import org.rendersnake.internal.ContextMap;


public class HttpRequestHandlerTest extends TestCase {

    HttpRequestHandler h = new HttpRequestHandler();
    PageContext context = new PageContext();
    
    public void test_shareQueryParametersInto() {
        
        h.shareQueryParametersInto("?a=b", context);
        ContextMap map = context.getContextMap(PageContext.REQUEST_PARAMETERS);
        Assert.assertNotNull(map.getString("a"));
        Assert.assertNull(map.getString("b"));
        Assert.assertEquals("b",map.getString("a"));
    }
    public void test_shareBodyParametersInto() {
        
        h.shareBodyParametersInto("a=b", context);
        ContextMap map = context.getContextMap(PageContext.REQUEST_PARAMETERS);
        Assert.assertNotNull(map.getString("a"));
        Assert.assertNull(map.getString("b"));
        Assert.assertEquals("b",map.getString("a"));
    }    
}
