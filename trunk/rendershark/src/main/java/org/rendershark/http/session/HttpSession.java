package org.rendershark.http.session;

import java.util.UUID;

import org.rendersnake.internal.SimpleContextMap;

public class HttpSession extends SimpleContextMap {
	private static final long serialVersionUID = 8823790451870465292L;

	private String id = UUID.randomUUID().toString();
    private boolean isValid = true;
    private SessionManager sessionManager;
	
	public HttpSession(SessionManager whoCreatedMe) {
	    super();
	    this.sessionManager = whoCreatedMe;
	}	
	
	public String toString() {
		return super.toString() + "(id=" + id + ")";
	}
    public String getId() {
        return id;
    }	
    public void invalidate() {
        this.isValid = false;
        sessionManager.clearSession(id);
    }
    public boolean isValid() { return isValid; }
}
