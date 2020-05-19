package com.tng.security.mobileserver.model;

import java.io.Serializable;

public class LoginResponse implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String loginSessionToken;

	public LoginResponse(String loginSessionToken) {
		this.loginSessionToken = loginSessionToken;
	}

	public String getLoginSessionToken() {
		return this.loginSessionToken;
	}
}