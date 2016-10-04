package com.simon.session.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
* @ClassName: CookieUtils  
* @Description: cookie 工具类  
* @author simon 
* @date 2016年9月11日 下午12:36:34  
*
 */
public class CookieUtils {

    
    private static final String SESSION_ID = "SHAREDSESSIONID";
    
    protected static Logger log = LoggerFactory.getLogger(CookieUtils.class);
    
    
    /**
     * 将Session ID写到客户端的Cookie中
     * 
     * @param id
     *            Session ID
     * @param response
     *            HTTP响应
     * @return
     */
    public static Cookie writeSessionIdToCookie(String id, HttpServletRequest request,
            HttpServletResponse response, int expiry) {
    	id = DestUtil.encrypt(id, RequestAddressUtil.getIpAddr(request));
        Cookie cookie = findCookie(SESSION_ID, request);
        if (cookie == null) {
            cookie = new Cookie(SESSION_ID, id);
        }
        cookie.setValue(id);
        cookie.setMaxAge(expiry);
        cookie.setPath(StringUtils.isEmpty(request.getContextPath()) ? "/" : request.getContextPath());
        cookie.setHttpOnly(true); // to protect from XSS attack!
        response.addCookie(cookie);
        return cookie;
    }
    
    
    
    /**
     * 查询指定名称的Cookie
     * 
     * @param name
     *            cookie名称
     * @param request
     *            HTTP请求
     * @return
     */
    public static Cookie findCookie(String name, HttpServletRequest request) {
    
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        // 迭代查找
        for (int i = 0, n = cookies.length; i < n; i++) {
            if (cookies[i].getName().equalsIgnoreCase(name)) {
                return cookies[i];
            }
        }
        return null;
    }
    
    /**
     * 查询指定名称的Cookie值
     * 
     * @param name
     *            cookie名称
     * @param request
     *            HTTP请求
     * @return
     */
    public static String findCookieValue(String name, HttpServletRequest request) {
    
        Cookie cookie = findCookie(name, request);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }
    
    /**
     * 在Cookie中查找Session ID
     * 
     * @param request
     *            HTTP请求
     * @return
     */
    public static String findSessionId(HttpServletRequest request) {
    	if((findCookieValue(SESSION_ID, request) == null) || (RequestAddressUtil.getIpAddr(request) == null)){
    		return null;
    	}
    	return DestUtil.decrypt(findCookieValue(SESSION_ID, request), RequestAddressUtil.getIpAddr(request));
    }
}
