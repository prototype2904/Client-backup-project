package ru.backup.rest.service;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

/**
 * 
 * ���������� ������� ��������������
 * 
 * @author Stetskevich Roman
 *
 */
@Service
public class AuthServiceImpl implements AuthService {

	@Override
	public HttpHeaders authenticate(String username, String password) {

		//������������� � ����������� ������ � ������ � Base64
		String plainCreds = ((username == null) ? "" : username) + ":" + ((password == null) ? "" : password);
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);
		
		//���������� basic ����������� � �����
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);
		headers.add("Content-Type", "multipart/form-data");
		return headers;
	}

}
