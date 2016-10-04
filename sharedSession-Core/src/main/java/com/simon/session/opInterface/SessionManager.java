package com.simon.session.opInterface;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
* @ClassName: SessionManage  
* @Description: TODO(这里用一句话描述这个类的作用)  
* @author simon 
* @date 2016年9月12日 下午5:03:08  
*
 */
public interface SessionManager {
	
	 /** Cookie的过期时间，默认30天 */
    public static final int COOKIE_EXPIRY = 30 * 24 * 60 * 60;
    
    public static final String SESSION_NAME = "tsid";
    
    /**
     * 返回指定ID的HttpSession对象
     * 
     */
    public HttpSession getHttpSession(String id);
    
    /**
     * 创建一个新的HttpSession对象
     * 
     */
    public HttpSession newHttpSession(HttpServletRequest request, HttpServletResponse response);
    
    public void setServletContext(ServletContext sc);
    
    /**
     * 
     * 获取session客户端接口 返回类型  
     */
    public SessionClient getSessionClient();
    
    /**
     * close the SessionManager instance
     * implementation can do some necessary clean work here, such as closing connection, etc.
     */
    public void close() throws Exception;
    

    
}
