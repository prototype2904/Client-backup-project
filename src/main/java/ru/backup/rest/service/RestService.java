package ru.backup.rest.service;

import org.springframework.http.HttpEntity;

import ru.backup.domain.ApplicationURLs;
import ru.backup.domain.TaskFromServer;

/**
 * Сервис, для рест запросов на сервер
 * @author Roman
 *
 */
public interface RestService {

	/**
	 * получить задачу с сервера
	 * @param url
	 * @param httpEntity
	 * @return
	 */
	TaskFromServer getTaskFromServer(ApplicationURLs url, HttpEntity<?> httpEntity);

	/**
	 * отправить объект на сервер
	 * @param url
	 * @param httpEntity
	 * @return
	 */
	public String sendObject(ApplicationURLs url, HttpEntity<?> httpEntity);


}
