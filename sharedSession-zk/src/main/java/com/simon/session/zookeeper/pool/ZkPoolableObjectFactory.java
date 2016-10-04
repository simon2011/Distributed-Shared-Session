package com.simon.session.zookeeper.pool;

import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simon.session.utils.ConfigUtil;

public class ZkPoolableObjectFactory implements PoolableObjectFactory {
	
	 private static final Logger log = LoggerFactory.getLogger(ZkPoolableObjectFactory.class);
	 private volatile KeeperState state = KeeperState.SyncConnected;
	 
	@Override
	public void activateObject(Object arg0) throws Exception {
		// TODO Auto-generated method stub

	}


	public void destroyObject(ZkClient obj) throws Exception {
		// TODO Auto-generated method stub
		  if (obj != null) {
	            obj.close();
	            if (log.isInfoEnabled()) {
	                log.info("Zookeeper客户端对象被关闭...");
	            }
	        }
	}


    @Override
	public void destroyObject(Object obj) throws Exception {
		destroyObject((ZkClient)obj);
	}

	@Override
	public ZkClient makeObject() throws Exception {
		// TODO Auto-generated method stub
		   //连接服务端
        String servers = ConfigUtil.SERVERS;
        int  connectionTimeout = ConfigUtil.CONNECTION_TIMEOUT*1000;
        ZkClient client = new ZkClient(servers , connectionTimeout);
		client.subscribeStateChanges(new IZkStateListener() {
			@Override
			public void handleStateChanged(KeeperState state) throws Exception {
				ZkPoolableObjectFactory.this.state = state;
				if (state == KeeperState.Disconnected) {
					log.info("Zookeeper客户端对象链接失败...,状态："+state);
				} else if (state == KeeperState.SyncConnected) {
					//stateChanged(StateListener.CONNECTED);
					log.info("Zookeeper客户端对象链接正常...,状态："+state);
				}
			}
			@Override
			public void handleNewSession() throws Exception {
				log.info("Zookeeper重新建立链接,状态："+state);
			}
			@Override
			public void handleSessionEstablishmentError(final Throwable error) throws Exception{
				 
			}
		});
		return client;
	}

	@Override
	public void passivateObject(Object arg0) throws Exception {
		// TODO Auto-generated method stub

	}


	
	@Override
	public boolean validateObject(Object obj) {
		return true;
	}
}
