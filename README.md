# Distributed-Shared-Session
分布式共享session
##设计
通过配置Filter，代理实现了Servlet规范(3.0)中HttpSession接口以及HttpServletRequest接口的部分方法。
##配置
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
