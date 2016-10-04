package com.simon.session.zookeeper;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simon.session.metadata.DSSession;
import com.simon.session.opInterface.AbstractSessionManager;
import com.simon.session.opInterface.SessionClient;
import com.simon.session.utils.ConfigUtil;
import com.simon.session.utils.CookieUtils;
import com.simon.session.zookeeper.sessionClient.ZkSessionClient;

public class ZKSessionManager extends AbstractSessionManager {
	
	private static final Logger log = LoggerFactory.getLogger(ZKSessionManager.class);
	private Lock sessionLock = new ReentrantLock();
    /** ZK客户端操作 */
    protected SessionClient client;
	
    /**
     * 构造方法
     * @param config
     */
    public ZKSessionManager() {
      
        client = ZkSessionClient.getInstance();
        // 建立Zookeeper的根节点
        client.createSession(null);
        
        log.info("创建SESSIONS组节点完成&&初始化zk链接池");

        
    }
    
    
    
    /**
     * 这个方法存在线程安全性问题，必须加上同步机制
     */
    @Override 
    public HttpSession getHttpSession(String id) {
    
        DSSession session = client.getSession(id);
        
        if (session == null || !session.isValid()) {
            try {
                sessionLock.lock();
                if (session != null) {
                        log.debug(">>>>>>>>>>> Try removing expired session: {}" , session);
                    session.invalidate();
                }
                return null;
            } finally {
                sessionLock.unlock();
            }
        }
        session.setServletContext(super.getServletContext());
        client.updateSession(session);
        return session;
    }
    
    
    
    @Override
    public HttpSession newHttpSession(HttpServletRequest request, HttpServletResponse response) {
    
        String id = super.getNewSessionId(request); // 获取新的Session ID
        Cookie cookie = CookieUtils.writeSessionIdToCookie(id, request, response, COOKIE_EXPIRY);
        if (cookie != null) {
            log.info(">>>>>>>>>>> Write SHAREDSESSIONID to Cookie,name:[{}],value:[{}]" , cookie.getName(),cookie.getValue());
        }
        DSSession session = new DSSession(this.getSessionClient(), id );
        session.setServletContext(super.getServletContext());        
        int sessionTimeout = ConfigUtil.SESSION_TIMEOUT ;
        session.setMaxInactiveInterval(sessionTimeout * 60 * 1000); // 转换成毫秒
        // 在ZooKeeper服务器上创建session节点，节点名称为Session ID
        client.createSession(session);
        
        return session;
    }
    
    @Override
    public void close() throws Exception {
    	
    	ZkSessionClient.getInstance().close();
    }
    
    @Override
    public SessionClient getSessionClient() {
    
        return this.client;
    }
}
