package com.tng.security.mobileserver.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonRootName;

public class AuthRequest implements Serializable {

	private static final long serialVersionUID = 5926468573005150707L;
	
	private String password;
	private String loginSessionToken;
	
	//need default constructor for JSON Parsing
	public AuthRequest()
	{
		
	}

	public AuthRequest(String cif, String scope) {
		this.setPassword(password);
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLoginSessionToken() {
		return this.loginSessionToken;
	}

	public void setLoginSessionToken(String loginSessionToken) {
		this.loginSessionToken = loginSessionToken;
	}
}