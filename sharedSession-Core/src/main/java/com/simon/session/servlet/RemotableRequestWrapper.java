package com.simon.session.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simon.session.opInterface.SessionManager;
import com.simon.session.utils.CookieUtils;

public class RemotableRequestWrapper extends HttpServletRequestWrapper {
	
	 protected Logger logger = LoggerFactory.getLogger(RemotableRequestWrapper.class);
	 
	 private static final String CURENT_SESSION = "current.dssession";
	 private static final String NULL_SESSION = "__NULL_";
	 private static final String CURENT_SESSIONID = "current.dssessionid";
	 
	    

	public RemotableRequestWrapper(HttpServletRequest request) {
		super(request);
		// TODO Auto-generated constructor stub
	}
	
	
	
	    private SessionManager sessionManager;
	    
	    private HttpServletRequest request;
	    
	    // hold an HttpServletResponse instance in case a new Cookie would be generated & added to user-agent.
	    private HttpServletResponse response;
	    

	    
	    /**
	     * ���췽��
	     * 
	     * @param request
	     */
	    public RemotableRequestWrapper(HttpServletRequest request, HttpServletResponse response, SessionManager sessionManager) {
	    
	        super(request);
	        this.request = request;
	        this.response = response;
	        this.sessionManager = sessionManager;
	    }
	    

	    
	    @Override
	    public String getRequestedSessionId() {
	    	String sessionId = null;
	    	if(request.getParameter(CURENT_SESSIONID)!= null){
	    		sessionId = request.getParameter(CURENT_SESSIONID).toString();
	    	}
	        if(request.getAttribute(CURENT_SESSIONID) != null){
	        	sessionId =  request.getAttribute(CURENT_SESSIONID).toString();
	        }
	        if(sessionId == null){
	        	sessionId = CookieUtils.findSessionId(request);
	        }
	        System.out.println(">>>>>>>>>>>>>>>>>>>>>getSessionId： "+ sessionId);
	        return sessionId;
	    }
	    
	    
	    @Override
	    public HttpSession getSession(boolean create) {
	    
	        if (sessionManager == null) {
	            throw new IllegalStateException("SessionManager not initialized");
	        }
	        
	        Object s = request.getAttribute(CURENT_SESSION);	
	        if (!create && s != null) {
	        	  return NULL_SESSION.equals(s.toString()) ? null : (HttpSession) s;
	        }
	        String sessionid = this.getRequestedSessionId();
	        HttpSession session = null;
	        
	        if (sessionid != null) {
	            session = sessionManager.getHttpSession(sessionid);
	            if (session == null && !create) {
	            	 request.setAttribute(CURENT_SESSION, NULL_SESSION);
	                 return null;
	            }
	        }
	        if (session == null && create) {
	            session = sessionManager.newHttpSession(this.request, this.response);
	        }
	        request.setAttribute(CURENT_SESSIONID, session.getId());
	        request.setAttribute(CURENT_SESSION, session == null ? NULL_SESSION : session);
	        return session;
	    }
	    
	    @Override
	    public HttpSession getSession() {
	    
	        return getSession(true);
	    }
	    
	    @Override
	    public boolean isRequestedSessionIdValid() {
	    
	        return getSession(false) != null;
	    }
	    
	    @Override
	    public boolean isRequestedSessionIdFromCookie() {
	    
	        return true;
	    }
	    
	    @Override
	    public boolean isRequestedSessionIdFromURL() {
	    
	        return false;
	    }
	    
	    @Override
	    public boolean isRequestedSessionIdFromUrl() {
	    
	        return isRequestedSessionIdFromURL();
	    }

}
