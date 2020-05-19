package com.tng.security.mobileserver.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonRootName;

public class LoginRequest implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;
	
	private String cif;
	private String scope;
	
	//need default constructor for JSON Parsing
	public LoginRequest()
	{
		
	}

	public LoginRequest(String cif, String scope) {
		this.setCIF(cif);
		this.setScope(scope);
	}

	public String getCIF() {
		return this.cif;
	}

	public void setCIF(String cif) {
		this.cif = cif;
	}

	public String getScope() {
		return this.scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
}