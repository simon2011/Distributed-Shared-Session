package com.simon.session.zookeeper.pool;

import java.util.NoSuchElementException;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.StackObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simon.session.utils.ConfigUtil;

public class ZkPoolManager {
		
	  private static final Logger log = LoggerFactory.getLogger(ZkPoolManager.class);
	  
	    /** 单例 */
	    protected static ZkPoolManager instance;
	    
	    private ObjectPool pool;
	    
	    /**
	     * 返回单例的对象
	     * 
	     * @return
	     */
	    public static  ZkPoolManager getInstance() {
	    
	    	if (instance == null) {
				synchronized (log) {
					if (instance == null) {
						instance = new ZkPoolManager();
					}
				}
			}
			return instance;
	    }
	    
	    /**
	     * 构造方法
	     */
	    private ZkPoolManager() {
	    
	        PoolableObjectFactory factory = new ZkPoolableObjectFactory();
	        
	        // 初始化ZK对象池
	        int maxIdle = ConfigUtil.MAX_IDLE;
	        int initIdleCapacity = ConfigUtil.INIT_IDLE_CAPACITY;
	        pool = new StackObjectPool(factory, maxIdle, initIdleCapacity);// 对象构建池

	        if (log.isInfoEnabled()) {
	            log.info("Zookeeper连接池初始化完成");
	        }
	    }
	    
	    
		/**
		 * 将zkClient对象从对象池中取出
		 * 
		 * @return
		 */
		public ZkClient borrowObject() {

			if (pool != null) {
				try {
					ZkClient client = (ZkClient) pool.borrowObject();
					if (log.isDebugEnabled()) {
						log.debug("从zkClient连接池中返回连接，zk.hashCode ="
								+ client.hashCode());
					}
					return client;
				} catch (NoSuchElementException ex) {
					log.error("从zkClient连接池获取连接时发生异常：", ex);
				} catch (IllegalStateException ex) {
					log.error("从zkClient连接池获取连接时发生异常:", ex);
				} catch (Exception e) {
					log.error("从zkClient连接池获取连接时发生异常:", e);
				}
			}
			return null;
		}
		
		
	    /**
	     * 将ZK实例返回对象池
	     * 
	     * @param zk
	     */
	    public void returnObject(ZkClient zk) {
	    
	        if (pool != null && zk != null) {
	            try {
	                pool.returnObject(zk);
	                if (log.isDebugEnabled()) {
	                    log.debug("将连接返回Zookeeper连接池完毕，zk.hashCode=" + zk.hashCode());
	                }
	            } catch (Exception ex) {
	                log.error("将连接返回Zookeeper连接池时发生异常：", ex);
	            }
	        }
	    }
	    
	    /**
	     * 关闭对象池
	     */
	    public void close() {
	        if (pool != null) {
	            try {
	                pool.close();
	                if (log.isInfoEnabled()) {
	                    log.info("关闭Zookeeper连接池完成");
	                }
	            } catch (Exception ex) {
	                log.error("关闭Zookeeper连接池时发生异常：", ex);
	            }
	        }
	    }
	    
}
