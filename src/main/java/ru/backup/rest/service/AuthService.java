package ru.backup.rest.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public interface AuthService {
	
	HttpHeaders authenticate(String username, String password);

}
