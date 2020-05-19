package com.tng.security.mobileserver.model;

import java.io.Serializable;

public class TngSession implements Serializable {

	private static final long serialVersionUID = 5926468583005150345L;
	
	private String jwt;
	private long lastUseTime;
	
	//need default constructor for JSON Parsing
	public TngSession()
	{
		
	}

	public TngSession(String jwt) {
		this.setJwt(jwt);
		this.setLastUseTime(System.currentTimeMillis());
	}

	public String getJwt() {
		return this.jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public long getLastUseTime() {
		return this.lastUseTime;
	}

	public void setLastUseTime(long lastUseTime) {
		this.lastUseTime = lastUseTime;
	}
	
	public String toString() {
		return jwt + '|' + lastUseTime;
	}
}