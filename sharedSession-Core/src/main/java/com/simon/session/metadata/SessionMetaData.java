package com.simon.session.metadata;

import java.io.Serializable;

public class SessionMetaData implements Serializable {
	
	/**  
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)  
	*/ 
	private static final long serialVersionUID = 1L;

	/** session ID.*/
	private String id;
	
	 /** session的创建时间 */
    private Long creationTime;
    
    /** session的最后一次访问时间 */
    private Long lastAccessedTime;
    
    /** session的最大空闲时间 */
    private int maxInactiveInterval;
    
    /** 当前版本 */
    private int version = 0;

    
    public SessionMetaData() {
        
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = this.creationTime;
    }
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Long creationTime) {
		this.creationTime = creationTime;
	}

	public Long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setLastAccessedTime(Long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
    
   /**
    * 
   * @Title: isValid  
   * @Description: 当前sessionMetadata是否有效 
   * @param @return    设定文件  
   * @return Boolean    返回类型  
   * @throws
    */
	 public Boolean isValid() {
	        
         return (getLastAccessedTime() + getMaxInactiveInterval()) > System.currentTimeMillis();
     }
}
