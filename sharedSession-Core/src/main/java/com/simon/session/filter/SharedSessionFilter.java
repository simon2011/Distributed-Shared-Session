package com.simon.session.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simon.session.opInterface.SessionManager;
import com.simon.session.servlet.RemotableRequestWrapper;

public class SharedSessionFilter implements Filter {
	 private static Logger logger =   LoggerFactory.getLogger(SharedSessionFilter.class.getName());
	 private SessionManager sessionManager;
	 
	 private static  String[] ignoreUrls={};
	 
	@Override
	public void destroy() {
        if (sessionManager != null) {
            try {
                sessionManager.close();
                logger.info(">>>>>>>>>>> TCSessionFilter.destroy completed.");
            } catch (Exception ex) {
                logger.error("==========> Error occurs when closing TCSessionManager. ", ex);
            }
        }

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		  if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
	            
	            HttpServletRequest req = (HttpServletRequest) request;
	            HttpServletResponse resp = (HttpServletResponse) response;
	            
	            String igurl = req.getRequestURI().toLowerCase();
	        	
	        	
    	    	if(ArrayUtils.contains(ignoreUrls, igurl)){
    	    		chain.doFilter(request, response);
    				return;
    	    	}
	       
	            
	            chain.doFilter(new RemotableRequestWrapper(req, resp, sessionManager), response);
	           
	        } else {
	            chain.doFilter(request, response);
	        }

	}

	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		 ServletContext sc = filterConfig.getServletContext();
		 
		    //sc.getSessionCookieConfig().setName(SESSION_NAME);
	        try {
	        	String initIgnoreUrls = filterConfig.getInitParameter("ignoreUrls");
	        	if(initIgnoreUrls!=null && !"".equals(initIgnoreUrls)) ignoreUrls = initIgnoreUrls.split(";");
	            this.sessionManager = (SessionManager) Class.forName("com.simon.session.zookeeper.ZKSessionManager").newInstance();
	        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
	            logger.error("==========> Error occurs when initializing TCSessionManager. ", e);
	            throw new ServletException(e);
	        }
	        logger.info("======> SharedSessionFilter.init completed.");
	        this.sessionManager.setServletContext(sc);

	}

}
