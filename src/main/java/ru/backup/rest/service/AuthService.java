package ru.backup.rest.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 * ������ ��� ����������� ������������
 * 
 * @author Stetskevich Roman
 *
 */
public interface AuthService {
	
	/**
	 * ������� HTTP-����� � Basic ������������ ������������ � ������� ������� � �������
	 * @param username - �����
	 * @param password - ������
	 * @return - Http �����
	 */
	HttpHeaders authenticate(String username, String password);

}
