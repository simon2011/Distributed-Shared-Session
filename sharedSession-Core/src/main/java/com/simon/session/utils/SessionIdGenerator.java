package com.simon.session.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
* @ClassName: SessionIdGenerator  
* @Description: 生成sessionId  
* @author simon 
* @date 2016年9月11日 下午3:06:23  
*
 */
public class SessionIdGenerator {

	  private static Logger log = LoggerFactory.getLogger(SessionIdGenerator.class);
	  private static Random random;
	  private static SessionIdGenerator instance;
	  private final static String SESSION_ID_RANDOM_ALGORITHM = "SHA1PRNG";
	  private final static String SESSION_ID_RANDOM_ALGORITHM_ALT = "IBMSecureRandom";
	  private final static String __NEW_SESSION_ID = "current.dssessionid";

	private boolean weakRandom;
		
	  private SessionIdGenerator() {
		  if (random == null) {
	            try {
	                random = SecureRandom.getInstance(SESSION_ID_RANDOM_ALGORITHM);
	                weakRandom = false;
	            } catch (NoSuchAlgorithmException e) {
	                try {
	                    random = SecureRandom.getInstance(SESSION_ID_RANDOM_ALGORITHM_ALT);
	                    weakRandom = false;
	                } catch (NoSuchAlgorithmException e_alt) {
	                    log.warn("==========> Failed in using {} and {} algorithm as random sessionid generator! As an alternative, degrading to java.util.Random.",SESSION_ID_RANDOM_ALGORITHM, SESSION_ID_RANDOM_ALGORITHM_ALT);
	                    random = new Random();
	                    weakRandom = true;
	                }
	            }
	        }
	        random.setSeed(random.nextLong() ^ System.currentTimeMillis()
	                ^ hashCode() ^ Runtime.getRuntime().freeMemory());
	  }
	  
	public static SessionIdGenerator getInstance() {
		if (instance == null) {
			synchronized (log) {
				if (instance == null) {
					instance = new SessionIdGenerator();
				}
			}
		}
		return instance;
	}
	
	public synchronized String newSessionId(HttpServletRequest request) {

		// A requested session ID can only be used if it is in use already.
		String requestedId = request.getRequestedSessionId();

		if (requestedId != null) {
			return requestedId;
		}

		// Else reuse any new session ID already defined for this request.
		String newId = (String) request.getAttribute(__NEW_SESSION_ID);
		if (newId != null) {
			return newId;
		}

		// pick a new unique ID!
		String id = null;
		while (id == null || id.length() == 0) {
			long r = weakRandom ? (hashCode()
					^ Runtime.getRuntime().freeMemory() ^ random.nextInt() ^ (((long) request
					.hashCode()) << 32)) : random.nextLong();
			r ^= System.currentTimeMillis();
			if (request.getRemoteAddr() != null) {
				r ^= request.getRemoteAddr().hashCode();
			}
			if (r < 0) {
				r = -r;
			}
			id = Long.toString(r, 36);
		}
		request.setAttribute(__NEW_SESSION_ID, id);
		return id;
	}
	
}
