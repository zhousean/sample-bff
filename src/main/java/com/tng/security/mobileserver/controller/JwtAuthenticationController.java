package com.tng.security.mobileserver.controller;

import java.util.StringTokenizer;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.tng.security.mobileserver.config.LettuceConnectionFactory;
import com.tng.security.mobileserver.model.AuthRequest;
import com.tng.security.mobileserver.model.AuthResponse;
import com.tng.security.mobileserver.model.LoginRequest;
import com.tng.security.mobileserver.model.LoginResponse;
import com.tng.security.mobileserver.model.TngSession;
import com.tng.security.mobileserver.util.JwtTokenUtil;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

@RestController
@CrossOrigin
public class JwtAuthenticationController {

	@Value("${jwt.encryptsecret}")
	private String encryptSecret;
	
	@Value("${jwt.encryptsalt}")
	private String encryptSalt;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {
		
		/////////////////////////////
		//
		// CIF validation
		//
		/////////////////////////////
		if( loginRequest.getCIF() == null || loginRequest.getCIF().length() == 0)
			throw new BadCredentialsException("MISSING_CLIENTID");
		String scope = loginRequest.getScope();
		if( scope == null || scope.length() == 0)
			scope = "anonymous";

		String loginSession = loginRequest.getCIF() + "|" + scope;
		
		TextEncryptor textEncryptor = Encryptors.text(encryptSecret, encryptSalt);
		String loginSessionToken = textEncryptor.encrypt(loginSession);

		return ResponseEntity.ok(new LoginResponse(loginSessionToken));
	}

	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public ResponseEntity<?> auth(@RequestBody AuthRequest authRequest) throws Exception {

		String loginSessionToken = authRequest.getLoginSessionToken();

		TextEncryptor textEncryptor = Encryptors.text(encryptSecret, encryptSalt);
		String loginSession = textEncryptor.decrypt(loginSessionToken);
		
		StringTokenizer tokenizer = new StringTokenizer(loginSession, "|");
		if( tokenizer.countTokens() != 2)
			throw new BadCredentialsException("INVALID_CREDENTIALS");
		
		String cif = tokenizer.nextToken();
		String scope = tokenizer.nextToken();
		
		///////////////////////////////
		//
		// Validate password
		//
		///////////////////////////////
		
		//authenticate(cif, scope);

		//final UserDetails userDetails = userDetailsService.loadUserByUsername(scope);

		final String token = jwtTokenUtil.generateToken(cif, scope);
		
		TextEncryptor jwtEncryptor = Encryptors.text(encryptSecret, encryptSalt);
		String encryptedToken = jwtEncryptor.encrypt(token);
		
		// Redis session code
		StatefulRedisConnection<String, String> connection = LettuceConnectionFactory.getConnection();;
		RedisCommands<String, String> syncCommands = connection.sync();		
		TngSession session = new TngSession(encryptedToken);
		UUID uuid = UUID.randomUUID();
		syncCommands.set(uuid.toString(), session.toString());

		return ResponseEntity.ok(new AuthResponse(uuid.toString()));
	}

}