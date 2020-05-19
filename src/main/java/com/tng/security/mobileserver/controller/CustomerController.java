package com.tng.security.mobileserver.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
	

	@RequestMapping({ "/v1/customers/name" })
	@PreAuthorize("hasAuthority('ROLE_LEVEL1') or hasAuthority('ROLE_LEVEL2')")
	public String guestPage() {

		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		return "response from /v1/customers/name, level1 or level2 required. Your username is " + name;
	}

	@RequestMapping({ "/v1/customers/transactions" })
	@PreAuthorize("hasAuthority('ROLE_LEVEL2')")
	public String userPage() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		return "response from /v1/customers/name, level2 required. Your username is " + name;
	}

	@RequestMapping({ "/v1/general/reference-data" })
	//@PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
	public String newPage() {

		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		return "response from /v1/general/reference_data, open to anonymous. Your username is " + name;
	}

}