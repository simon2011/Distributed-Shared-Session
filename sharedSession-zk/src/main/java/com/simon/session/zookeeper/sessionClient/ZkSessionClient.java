package com.simon.session.zookeeper.sessionClient;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simon.session.metadata.DSSession;
import com.simon.session.metadata.SessionMetaData;
import com.simon.session.opInterface.SessionClient;
import com.simon.session.zookeeper.pool.ZkPoolManager;


/**
 * 
* @ClassName: ZkSessionClient  
* @Description: zk操作session
* @author simon 
* @date 2016年9月30日 下午4:54:36  
*
 */
public class ZkSessionClient  implements SessionClient {
  
	 /** 日志 */
    private static final Logger log = LoggerFactory.getLogger(ZkSessionClient.class);
    
    /** ZK组节点名称 */
    public static final String GROUP_NAME = "/DT_SESSIONS";
    public static final String NODE_SEP = "/";
    
	private static ZkPoolManager pool = null;
	/** 单例对象 */
	private static ZkSessionClient instance;
    /**
     * 构造方法
     */
    private ZkSessionClient() {
    
        if (pool == null) {
            pool = ZkPoolManager.getInstance();
        }
    }
    
    
    /**
     * 返回单例方法
     * 
     * @return
     */
	public static ZkSessionClient getInstance() {
		if (instance == null) {
			synchronized (log) {
				if (instance == null) {
					instance = new ZkSessionClient();
				}
			}
		}
		return instance;
	}
    
	@Override
	public boolean createSession(DSSession session) {
		
		
		 ZkClient zkClient = pool.borrowObject();
         if (zkClient == null) {
            log.error("从连接池中获取连接时，发生错误");
            return false;
         }
         try{
        	 if(session == null ){
        		 if(!zkClient.exists(GROUP_NAME))
        			 zkClient.createPersistent(GROUP_NAME);
        	 }else{
        		 SessionMetaData metadata = session.getSessionMetadata();
            	 String path = metadata == null ? GROUP_NAME : (GROUP_NAME + NODE_SEP + metadata.getId());
            	 zkClient.createPersistent(path, metadata);
        	 }
        	
         }catch(Exception e){
        	  log.error("创建session失败："+e);
        	 return false;
         }finally{
        	 pool.returnObject(zkClient);
         }
         return true;
	}

	


	@Override
	public DSSession getSession(String sessionId) {
		  log.debug(">>> Try to get sessionid: " + sessionId);
		  ZkClient zkClient = pool.borrowObject();
	         if (zkClient == null) {
	            log.error("从连接池中获取连接时，发生错误");
	            return null;
	         }
	        String path = GROUP_NAME + NODE_SEP + sessionId;
	        
	        SessionMetaData metadata = null;
	        try {
	        	Stat stat = new Stat();  
	        	metadata = zkClient.readData(path, stat);
	            if (metadata == null) {
	                log.debug(">>> Failed to get sessionid: " + sessionId);
	                return null;
	            }
	            return new DSSession(this, metadata , false);
	        } catch (Exception e) {
	            log.error("Error in fetching session from ZooKeeper Server. id: " + sessionId, e);
	        } finally {
	            pool.returnObject(zkClient);
	        }
	        return null;
	}

	@Override
	public List<String> getSessions() {
	        
	        ZkClient zkClient = pool.borrowObject();
	         if (zkClient == null) {
	            log.error("从连接池中获取连接时，发生错误");
	            return null;
	         }
	        String path = GROUP_NAME ;
	        
	        try {
	            return zkClient.getChildren( path );
	        } catch (Exception e) {
	         
	        	log.error("Error in getting session id list... ", e);
	            return null;
	        } finally {
	            pool.returnObject(zkClient);
	        }
	}
	
	@Override
	public Map<String, Object> removeSession(String sessionId) {
	   ZkClient zkClient = pool.borrowObject();
         if (zkClient == null) {
            log.error("从连接池中获取连接时，发生错误");
            return null;
         }
        String path = GROUP_NAME + NODE_SEP + sessionId;
        
        HashMap<String, Object> datas = new HashMap<String, Object>();
        try {
            datas = (HashMap<String, Object>) this.getNamesAndAttributes(sessionId);
            zkClient.deleteRecursive(path);
        } catch (Exception e) {
            log.error("Error in removing session: sessionid " + sessionId, e);
            return null;
        } finally {
            pool.returnObject(zkClient);
        }       
        log.debug("删除Session节点完成:[" + path + "]");       
        return datas;
	}

	@Override
	public Map<String, Object> getNamesAndAttributes(String sessionId) {
		   ZkClient zkClient = pool.borrowObject();
	         if (zkClient == null) {
	            log.error("从连接池中获取连接时，发生错误");
	            return null;
	         }
	        String path = GROUP_NAME + NODE_SEP + sessionId;
	        
	        HashMap<String, Object> datas = new HashMap<String, Object>();
	        try {
	            List<String> nodes = zkClient.getChildren(path);
	            if (nodes != null) {
	            	Stat stat = new Stat();  
	                for (String node : nodes) {
	                    String dataPath = path + NODE_SEP + node;
	                    // 获取数据	                   	                	
	                    Object obj = zkClient.readData(dataPath, stat);
	                    datas.put(node, obj);
	                }
	            }
	        } catch (Exception e) {
	            log.error("Error in getNamesAndAttributes session: sessionid " + sessionId, e);
	            return null;	       
	        } finally {
	            pool.returnObject(zkClient);
	        }
	        return datas;
	}

	@Override
	public boolean updateSession(DSSession session) {
		
		  SessionMetaData metadata = session.getSessionMetadata();
			ZkClient zkClient = pool.borrowObject();
	        if (zkClient == null) {
	            log.error("从连接池中获取连接时，发生错误");
	            return false;
	        }
	        try {
	            // //设置当前版本号
	            // metadata.setVersion(stat.getVersion());
	            String path = GROUP_NAME + NODE_SEP + metadata.getId();
	            metadata.setLastAccessedTime(System.currentTimeMillis());
	            zkClient.writeData(path, metadata);
	            log.debug("更新Session节点的元数据完成[{}]" , path);
	            
	            return true;
	        } catch (Exception e) {
	            log.error("Error in fetching session from ZooKeeper Server. id: " + metadata.getId(), e);
	        } finally {
	        	 pool.returnObject(zkClient);
	        }
	        return true;
	}

	@Override
	public boolean setAttribute(String sessionId, String key, Serializable value) {
		ZkClient zkClient = pool.borrowObject();
        if (zkClient == null) {
            log.error("从连接池中获取连接时，发生错误");
            return false;
        }
        String path = GROUP_NAME + NODE_SEP + sessionId + NODE_SEP + key;
      
        try{
	    	  if(zkClient.exists(path)){
	          	zkClient.writeData(path, value);
	          }else{
	        	zkClient.createPersistent(path, value);
	          }
        }catch(Exception e){
        	log.error("存储session数据错误："+e);
        }finally{
        	  pool.returnObject(zkClient);
        }
		return true;
	}

	@Override
	public Object getAttribute(String sessionId, String key) {

		 ZkClient zkClient = pool.borrowObject();
	     if (zkClient == null) {
	        log.error("从连接池中获取连接时，发生错误");
	        return false;
	     }
        
        String path = GROUP_NAME + NODE_SEP + sessionId + NODE_SEP + key;
        
        Object obj = null;

        try {
        	Stat stat = new Stat();  
        	obj = zkClient.readData(path, stat);
        } catch (Exception e) {
          log.error("Error in getting attribute. sessionid " + sessionId + " key: " + key, e);
          return null;
        } finally {
            pool.returnObject(zkClient);
        }
       
        return obj;
	}
	
	

	@Override
	public List<String> getAttributeNames(String sessionId) {

		 ZkClient zkClient = pool.borrowObject();
         if (zkClient == null) {
            log.error("从连接池中获取连接时，发生错误");
            return null;
         }
        String path = GROUP_NAME + NODE_SEP + sessionId;
        
        try {
            DSSession session = this.getSession(sessionId);
            if (session == null || !session.isValid()) {
                return null;
            }
            return zkClient.getChildren( path );
        } catch (Exception e) {
         
            log.error("Error in getting attribute name list: sessionid " + sessionId, e);
            return null;
        } finally {
            pool.returnObject(zkClient);
        }
	}
	
	@Override
	public boolean removeAttribute(String sessionId, String key) {

		 ZkClient zkClient = pool.borrowObject();
	     if (zkClient == null) {
	        log.error("从连接池中获取连接时，发生错误");
	        return false;
	     }
        String path = GROUP_NAME + NODE_SEP + sessionId + NODE_SEP + key;
        try {
            zkClient.delete(path);
        } catch (Exception e) {           
           log.error("Error in removing attribute: sessionid " + sessionId + " key: " + key, e);
           return false;
        } finally {
            pool.returnObject(zkClient);
        }
        return true;
	}
	
	public void close() {
		if (pool != null) {
			try {
				pool.close();
				log.info("关闭zk连接池完成");
			} catch (Exception ex) {
				log.error("关闭zk连接池时发生异常：", ex);
			}
		}

	}

}
