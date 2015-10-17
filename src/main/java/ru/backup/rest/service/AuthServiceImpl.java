package ru.backup.rest.service;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

	@Override
	public HttpHeaders authenticate(String username, String password) {
		if (username == null || password == null) {
			throw new NullPointerException("Username or password = null");
		}
		String plainCreds = username + ":" + password;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		headers.add("Content-Type", "application/json");
		return headers;
	}

}
