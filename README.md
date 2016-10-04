# Distributed-Shared-Session
分布式共享session

使用时修改web.xml 加入过滤器

 	 <filter>
	    <filter-name>SessionFilter</filter-name>
	    <filter-class>com.simon.session.filter.SharedSessionFilter</filter-class>
	</filter>
	<filter-mapping>
	    <filter-name>SessionFilter</filter-name>
	    <url-pattern>*</url-pattern>
	</filter-mapping>
