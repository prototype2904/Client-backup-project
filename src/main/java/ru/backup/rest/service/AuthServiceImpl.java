package ru.backup.rest.service;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

/**
 * 
 * Реализация сервиса аутентификации
 * 
 * @author Stetskevich Roman
 *
 */
@Service
public class AuthServiceImpl implements AuthService {

	@Override
	public HttpHeaders authenticate(String username, String password) {

		//представление и кодирование логина и пароля в Base64
		String plainCreds = ((username == null) ? "" : username) + ":" + ((password == null) ? "" : password);
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);
		
		//добавление basic авторизации в хедер
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		headers.add("Content-Type", "multipart/form-data");
		return headers;
	}

}
