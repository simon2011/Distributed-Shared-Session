package com.simon.session.opInterface;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.simon.session.metadata.DSSession;

public interface SessionClient {
		
	/**  
	* 获取所有的sessionid列表 
	 */
    public List<String> getSessions();
    
	 /**
	 * 获取session对象
	  */
    public DSSession getSession(String sessionid);
    
	  /**
	   *  更新session访问日期 
	   */
    public boolean updateSession(DSSession session);
    
    /**
     * 创建一个新的session节点
     */
    public boolean createSession(DSSession session);
    
    /**
     * 增加session的新属性值对
     */
    public boolean setAttribute(String sessionid, String key, Serializable value);
    
    /** 
     * 获取session的属性值
     */
    public Object getAttribute(String sessionid, String key);
    
    /**
     * 删除session的指定key
     */
    public boolean removeAttribute(String sessionid, String key);
    
    /**
     * 获取session的属性名称列表
     */
    public List<String> getAttributeNames(String sessionid);
    
    /**
     * 移除session
     */
    public Map<String, Object> removeSession(String sessionid);
    
    
    /**
     * 获取session的属性名和值
     */
    public Map<String ,Object> getNamesAndAttributes(String sessionid);
}
