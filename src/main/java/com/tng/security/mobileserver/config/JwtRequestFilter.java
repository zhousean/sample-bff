package com.tng.security.mobileserver.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tng.security.mobileserver.model.TngSession;
import com.tng.security.mobileserver.util.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Value("${jwt.encryptsecret}")
	private String encryptSecret;
	
	@Value("${jwt.encryptsalt}")
	private String encryptSalt;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");

		// Redis session code
		StatefulRedisConnection<String, String> connection = LettuceConnectionFactory.getConnection();;			
		RedisCommands<String, String> syncCommands = connection.sync();
		String sessionId = null;
		
		String username = null;
		String encryptedJwtToken = null;
		String jwtToken = null;
		// JWT Token is in the form "Bearer token". Remove Bearer word and get
		// only the Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			sessionId = requestTokenHeader.substring(7);
			
			// Retrieve JWT and its lastUseTime from Redis
			connection = LettuceConnectionFactory.getConnection();;			
			syncCommands = connection.sync();
			String sessionString = syncCommands.get(sessionId);
			int i = sessionString.indexOf('|');
			encryptedJwtToken = sessionString.substring(0, i);
			long lastUseTime = Long.parseLong(sessionString.substring(i+1));
			
			// Check if the session is expired
			long currentTime = System.currentTimeMillis();
			if(currentTime - lastUseTime > 1000*60*20) {
				System.out.println("Session expired.");
				encryptedJwtToken = null;
				syncCommands.del(sessionId);
			}
			else {
				TextEncryptor textEncryptor = Encryptors.text(encryptSecret, encryptSalt);
				jwtToken = textEncryptor.decrypt(encryptedJwtToken);
				
				try {
					username = jwtTokenUtil.getUsernameFromToken(jwtToken);
				} catch (IllegalArgumentException e) {
					System.out.println("Unable to get JWT Token");
				} catch (ExpiredJwtException e) {
					System.out.println("JWT Token has expired");
				}
			}

			// Once we get the token validate it.
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	
				// if token is valid configure Spring Security to manually set
				// authentication
				if (jwtTokenUtil.validateToken(jwtToken, username)) {
					String scope = jwtTokenUtil.getScopeFromToken(jwtToken);
					String role = null;
					if(scope.equals("level1"))
						role = "ROLE_LEVEL1";
					else if(scope.equals("level2"))
						role = "ROLE_LEVEL2";
					else
						role = "";
		
					SecurityContext context = SecurityContextHolder.createEmptyContext(); 
					Authentication authentication = new TngAuthenticationToken(username, null, role); 
					context.setAuthentication(authentication);
					SecurityContextHolder.setContext(context);
		
					// Redis session code
					TngSession session = new TngSession(encryptedJwtToken);
					syncCommands.set(sessionId, session.toString());
				}
			}
			
		} else { // not session id in the request header, set it to anonymous
			SecurityContext context = SecurityContextHolder.createEmptyContext(); 
			Authentication authentication = new TngAuthenticationToken("anonymous", null, "ROLE_ANONYMOUS"); 
			context.setAuthentication(authentication);
			SecurityContextHolder.setContext(context);
		}
		
		chain.doFilter(request, response);
	}

}