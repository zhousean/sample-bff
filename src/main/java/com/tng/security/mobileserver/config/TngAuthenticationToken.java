package com.tng.security.mobileserver.config;

import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.AuthorityUtils;

public class TngAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	private final Object credentials;
	private final Object principal;

	// ~ Constructors
	// ===================================================================================================

	public TngAuthenticationToken(Object principal, Object credentials) {
		super(null);
		this.principal = principal;
		this.credentials = credentials;
	}

	public TngAuthenticationToken(Object principal, Object credentials, String... authorities) {
		this(principal, credentials, AuthorityUtils.createAuthorityList(authorities));
	}

	public TngAuthenticationToken(Object principal, Object credentials, List<GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		setAuthenticated(true);
	}

	// ~ Methods
	// ========================================================================================================

	public Object getCredentials() {
		return this.credentials;
	}

	public Object getPrincipal() {
		return this.principal;
	}
}