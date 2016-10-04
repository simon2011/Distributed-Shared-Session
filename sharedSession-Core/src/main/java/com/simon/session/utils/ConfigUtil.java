package com.simon.session.utils;

import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigUtil {
	
    private static final Logger log = LoggerFactory.getLogger(ConfigUtil.class);
    
    private static final String config_file = "session.properties";
    
    /** 服务器地址 */
    public static String SERVERS;
    
    /** 初始连接数 */
    public static int MAX_IDLE;
    
    /** 连接池中，最小的空闲连接数 */
    public static int INIT_IDLE_CAPACITY;
    

    
    /** 和zk服务器建立的连接的超时时间 单位秒 */
    public static int CONNECTION_TIMEOUT;
    
    /** session的生命周期 单位分钟 */
    public static int SESSION_TIMEOUT;
    
    /** 检查任务的启动周期 */
    public static int TIMEOUT_CHECK_INTERVAL;
    
    
    
	private static final int defaultMaxIdle = 8;
    
    private static final int defaultInitIdleCapacity = 4;
    
    private static final int defaultSessionTimeout = 30;
    
    private static final int defaultConnectionTimeout = 60;
    
    private static final int defaultTimeoutCheckInterval = 30;
    
    static {
        InputStream in = ConfigUtil.class.getClassLoader().getResourceAsStream(config_file);
        Properties props = new Properties();
        try {
            if (in != null) {
                props.load(in);
            }
            SERVERS = props.getProperty("session.servers");
            MAX_IDLE = NumberUtils.toInt(props.getProperty("session.max_idle"), defaultMaxIdle);
            INIT_IDLE_CAPACITY = NumberUtils.toInt(props.getProperty("session.init_idle_capacity"), defaultInitIdleCapacity);
            SESSION_TIMEOUT = NumberUtils.toInt(props.getProperty("session.session_timeout"), defaultSessionTimeout);
            CONNECTION_TIMEOUT = NumberUtils.toInt(props.getProperty("session.connection_timeout"),defaultConnectionTimeout);
            TIMEOUT_CHECK_INTERVAL = NumberUtils.toInt(props.getProperty("session.timeout_check_interval"),defaultTimeoutCheckInterval);
        } catch (Exception e) {
            log.error("读取session配置文件时出错", e);
        }
    }
}
