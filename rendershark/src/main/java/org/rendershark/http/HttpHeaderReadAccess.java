package org.rendershark.http;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.rendersnake.internal.ContextMap;

public class HttpHeaderReadAccess implements ContextMap {

    HttpRequest request;

    public HttpHeaderReadAccess(HttpRequest request) {
        this.request = request;
    }

    @Override
    public Boolean getBoolean(String key, Boolean... optional) {
        throw new IllegalAccessError("Not Allowed");
    }

    @Override
    public Long getLong(String key, Long... optional) {
        throw new IllegalAccessError("Not Allowed");
    }

    @Override
    public Float getFloat(String key, Float... optional) {
        throw new IllegalAccessError("Not Allowed");
    }

    @Override
    public String getString(String key, String... optional) {
        String value = request.getHeader(key);
        if (value == null) {
            if (optional.length == 0) {
                return null;
            } else {
                return optional[0];
            }
        } else {
            return value;
        }
    }

    @Override
    public Object clear(String key) {
        throw new IllegalAccessError("Not Allowed");
    }

    @Override
    public HttpHeaderReadAccess withBoolean(String key, Boolean trueOrFalse) {
        throw new IllegalAccessError("Not Allowed");
    }

    @Override
    public HttpHeaderReadAccess withLong(String key, Long aLong) {
        throw new IllegalAccessError("Not Allowed");
    }

    @Override
    public HttpHeaderReadAccess withFloat(String key, Float aFloat) {
        throw new IllegalAccessError("Not Allowed");
    }

    @Override
    public Integer getInteger(String key, Integer... optional) {
        throw new IllegalAccessError("Not Allowed");    }

    @Override
    public HttpHeaderReadAccess withInteger(String key, Integer anInteger) {
        throw new IllegalAccessError("Not Allowed");
    }

    @Override
    public Object getObject(String key, Object... optional) {
        throw new IllegalAccessError("Not Allowed");
    }

    @Override
    public HttpHeaderReadAccess withObject(String key, Object value) {
        throw new IllegalAccessError("Not Allowed");        
    }

    @Override
    public HttpHeaderReadAccess withString(String key, String value) {
        throw new IllegalAccessError("Not Allowed");        
    }
}