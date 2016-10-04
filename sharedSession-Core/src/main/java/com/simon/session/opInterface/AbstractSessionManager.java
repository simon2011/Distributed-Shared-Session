package com.simon.session.opInterface;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.simon.session.metadata.DSSession;
import com.simon.session.utils.SessionIdGenerator;

public  abstract class AbstractSessionManager implements SessionManager {

	private ServletContext servletContext;
	
    /** 本地的session容器 */
    private static Map<String, DSSession> sessions;
    
    private SessionIdGenerator sessionIdGenerator;
    
    public AbstractSessionManager() {
        if (sessions == null) {
            sessions = new ConcurrentHashMap<String, DSSession>();
        }
        if (sessionIdGenerator == null) {
            sessionIdGenerator = SessionIdGenerator.getInstance();
        }
    }
	 
	@Override
	public HttpSession getHttpSession(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession newHttpSession(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SessionClient getSessionClient() {
		// TODO Auto-generated method stub
		return this.getSessionClient();
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}
	
	
	/**
	 * 
	* @Title: getNewSessionId  
	* @Description:获取新的sessionId
	* @param @param request
	* @param @return    设定文件  
	* @return String    返回sessionId 
	* @throws
	 */
    public String getNewSessionId(HttpServletRequest request) {
    
        if (sessionIdGenerator != null) {
            return sessionIdGenerator.newSessionId(request);
        }
        return null;
    }
	
	    
    public ServletContext getServletContext() {
    
        return servletContext;
    }
    
    public void setServletContext(ServletContext servletContext) {
    
        this.servletContext = servletContext;
    }

}
