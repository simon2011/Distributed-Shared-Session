package com.simon.session.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.simon.session.opInterface.SessionClient;
import com.simon.session.utils.ConfigUtil;




/**
 * 
* @ClassName: DSSession  
* @Description:HttpSession代理类  
* @author simon 
* @date 2016年9月12日 下午4:54:19  
*
 */
public class DSSession implements HttpSession  {

	private static final Logger logger = LoggerFactory.getLogger(DSSession.class);
	
	private SessionClient sessionClient;
	
	private boolean isNew = false;
		
    private SessionMetaData metadata;
    
    private ServletContext servletContext;
    
    private Map<String , Map<String , Object>> cache = new ConcurrentHashMap<String, Map<String, Object>>();
    

   

    /**
     * 构造方法,指定ID
     * 
     * @param sessionClient
     * @param id
     */
    public DSSession(SessionClient sessionClient, SessionMetaData metadata, boolean isNew) {
    
        this.sessionClient = sessionClient;
        this.metadata = metadata;
        this.isNew = isNew;
    }
    
    public DSSession(SessionClient sessionClient, String id) {
    
        this.sessionClient = sessionClient;
        this.metadata = new SessionMetaData();
        this.metadata.setId(id);
        this.isNew = true;
        fireListener(true);
    }
    

    
    public SessionMetaData getSessionMetadata() {
        
        return this.metadata;
    }
    
    /**
     * 
    * @Title: isValid  
    * @Description:判断当前session是否有效
    * @param @return    设定文件  
    * @return boolean    返回类型  
    * @throws
     */
    public boolean isValid() {
    	
        return getLastAccessedTime() + getMaxInactiveInterval() > System.currentTimeMillis();
    }
    /**
     * 被访问
     */
    public void access() {
    
        this.isNew = false;
        this.metadata.setLastAccessedTime(System.currentTimeMillis());
    }
    
	@Override
	public Object getAttribute(String name) {
	
		String sessionId = this.getId();
		if(StringUtils.isNotBlank(sessionId)){
			
			Object o = this.sessionClient.getAttribute(sessionId, name);
			return o;
			
		}
			
		return null;
	}

	@Override
	public void setAttribute(String name, Object value) {
		// TODO Auto-generated method stub
		  if (name == null) {
	            throw new IllegalArgumentException("attribute name cannot be null");
	        }
	        
	        if (value == null) {
	            removeAttribute(name);
	        } else if (!(value instanceof Serializable)) {
	        	logger.warn("the attribute value {} cannot implement Serializable in setting attribute" , value);
	            return;
	        }
	        
	        access();
	        try {
	          if(StringUtils.isNotBlank(getId())){
		            sessionClient.setAttribute(getId(), name, (Serializable) value);
	          }
	           
	        } catch (Exception ex) {
	            logger.error(String.format("==========> Error occurs when setting attribute: [%s] of session: [%s]", name, this), ex);
	        }
	        fireHttpSessionBindEvent(name, value);
	}
	
	@Override
	public void removeAttribute(String name) {
		// TODO Auto-generated method stub
		  if (name == null) {
	            throw new IllegalArgumentException("attribute name cannot be null");
	        }
	        
	        access();
	        Object value = null;
	        String id = getId();
	        if (StringUtils.isNotBlank(id)) {
	            try {
	                sessionClient.removeAttribute(id, name);
	            } catch (Exception ex) {
	                logger.error(String.format("Error occurs when removing attribute: [%s] from session: [%s]", name, this), ex);
	            }
	        }
	        fireHttpSessionUnbindEvent(name, value);
	}
	
	@Override
	public Enumeration<String> getAttributeNames() {
		// TODO Auto-generated method stub
        access();
        String id = getId();
        if (StringUtils.isNotBlank(id)) {
            try {
                List<String> names = sessionClient.getAttributeNames(id);
                if (names != null) {
                    return Collections.enumeration(names);
                }
            } catch (Exception ex) {
                logger.error("Error occurs when calling getAttributeNames from session: {}" ,this, ex);
            }
        }
        return null;
	}



   public void setServletContext(ServletContext servletContext) {
	    
        this.servletContext = servletContext;
    }
    
    @Override
    public ServletContext getServletContext() {
    
        return this.servletContext;
    }
	    

	@Override
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(String name) {
		
		 return getAttribute(name);
	}
    @Override
    public void putValue(String name, Object value) {
    
        setAttribute(name, value);
    }
    
    @Override
    public void removeValue(String name) {
    
        removeAttribute(name);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public String[] getValueNames() {
    
        List<String> names = new ArrayList<String>();
        Enumeration n = getAttributeNames();
        while (n.hasMoreElements()) {
            names.add((String) n.nextElement());
        }
        return names.toArray(new String[] {});
    }

	@Override
	public void invalidate() {
	
	   String id = getId();
        if (StringUtils.isNotBlank(id)) {
            try {
                Map<String, Object> sessionMap = sessionClient.removeSession(id);
                if (sessionMap != null && sessionMap.size() > 0) {
                    for (Map.Entry<String, Object> entry : sessionMap.entrySet()) {
                        fireHttpSessionUnbindEvent(entry.getKey(), entry.getValue());
                    }
                }
            } catch (Exception ex) {
                logger.error("==========> Error occurs when invalidating session: " + this, ex);
            }
        }
        fireListener(false);
		
	}
	


	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return isNew;
	}


    /**
     * 触发Session的事件
     */
    protected void fireHttpSessionBindEvent(String name, Object value) {
    
        // 处理Session的监听器
        if (value != null && value instanceof HttpSessionBindingListener) {
            HttpSessionBindingEvent event = new HttpSessionBindingEvent(this, name, value);
            ((HttpSessionBindingListener) value).valueBound(event);
        }
    }
    
    /**
     * 触发Session的事件
     */
    protected void fireHttpSessionUnbindEvent(String name, Object value) {
    
        // 处理Session的监听器
        if (value != null && value instanceof HttpSessionBindingListener) {
            HttpSessionBindingEvent event = new HttpSessionBindingEvent(this, name, value);
            ((HttpSessionBindingListener) value).valueUnbound(event);
        }
    }
    
    /**
     * 触发Session监听器
     */
    protected void fireListener(boolean isCreate) {
		List<HttpSessionListener> listeners = ConfigUtil.httpSessionListeners;
		if (listeners != null) {
			HttpSessionEvent event = new HttpSessionEvent(this);
			for (int i = 0; i < listeners.size(); i++) {
				HttpSessionListener listener = listeners.get(i);
				try {
					if (isCreate) {
						logger.debug("触发会话创建监听器，" + listener);
						listener.sessionCreated(event);
					} else {
						logger.debug("触发会话失效监听器，" + listener);
						listener.sessionDestroyed(event);
					}
				} catch (Throwable t) {
					logger.error("会话创建监听器调用失败！" + listener, t);
				}
			}
		}
	}
    
	@Override
	public long getCreationTime() {
		
		return this.metadata.getCreationTime();
	}

	@Override
	public String getId() {
		
		return this.metadata.getId();
	}

	@Override
	public long getLastAccessedTime() {
		
		return this.metadata.getLastAccessedTime();
	}

	@Override
	public int getMaxInactiveInterval() {
		
		return this.metadata.getMaxInactiveInterval();
	}

	@Override
	public void setMaxInactiveInterval(int maxInactiveInterval) {
		
		this.metadata.setMaxInactiveInterval(maxInactiveInterval);
	}

}
