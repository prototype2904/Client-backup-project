package ru.backup.rest.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ru.backup.domain.ApplicationURLs;
import ru.backup.domain.TaskForClient;
import ru.backup.domain.TaskFromServer;

/**
 * Сервис для рест запросов на сервер
 * @author Stetskevich Roman
 *
 */
public interface RestService {

	/**
	 * Получить задачу с сервера
	 * 
	 * @param url - ссылка на сервер
	 * @param httpEntity - 
	 * @return
	 * @throws IOException 
	 * @throws RestClientException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	List<TaskForClient> getTaskFromServer(ApplicationURLs url, HttpEntity<?> httpEntity) throws JsonParseException, JsonMappingException, RestClientException, IOException;

	/**
	 * отправить объект на сервер
	 * @param url
	 * @param httpEntity
	 * @return
	 */
	public String sendObject(ApplicationURLs url, HttpEntity<?> httpEntity);


}
