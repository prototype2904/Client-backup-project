package ru.backup.rest.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 * Сервис для авторизации пользователя
 * 
 * @author Stetskevich Roman
 *
 */
public interface AuthService {
	
	/**
	 * Вернуть HTTP-хедер с Basic авторизацией пользователя с данными логином и паролем
	 * @param username - логин
	 * @param password - пароль
	 * @return - Http хедер
	 */
	HttpHeaders authenticate(String username, String password);

}
