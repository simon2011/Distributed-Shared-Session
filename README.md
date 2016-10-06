# Distributed-Shared-Session
分布式共享session
##设计
通过配置Filter，代理实现了Servlet规范(3.0)中HttpSession接口以及HttpServletRequest接口的部分方法。
##配置
session.properties 配置相关
```
 #服务器地址
session.servers=192.168.224.128:2181,192.168.224.128:2182,192.168.224.128:2183
 #初始连接数
session.max_idle=8
#连接池中，最小的空闲连接数 
session.init_idle_capacity=4
#和zk服务器建立的连接的超时时间 单位秒
session.connection_timeout=5
#session的生命周期 单位分钟
session.session_timeout=5
#  检查任务的启动周期
session.timeout_check_interval=30
#session监听类，以逗号分隔,必须实现HttpSessionListener
session.sessionListener=
```

使用时修改web.xml 加入过滤器
```
 	 <filter>
	    <filter-name>SessionFilter</filter-name>
	    <filter-class>com.simon.session.filter.SharedSessionFilter</filter-class>
	</filter>
	<filter-mapping>
	    <filter-name>SessionFilter</filter-name>
	    <url-pattern>*</url-pattern>
	</filter-mapping>
```

