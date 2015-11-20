package ru.backup.rest.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ru.backup.domain.ApplicationURLs;
import ru.backup.domain.FileForm;
import ru.backup.domain.TaskForClient;
import ru.backup.domain.TaskFromServer;

/**
 * Сервис для рест запросов на сервер
 * 
 * @author Stetskevich Roman
 *
 */
public interface RestService {

	/**
	 * Получить задачу с сервера
	 * 
	 * @param url
	 *            - ссылка на сервер
	 * @param httpEntity
	 *            -
	 * @return
	 * @throws IOException
	 * @throws RestClientException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	List<TaskForClient> getTaskFromServer(HttpEntity<?> httpEntity)
			throws JsonParseException, JsonMappingException, RestClientException, IOException;

	/**
	 * отправить объект на сервер
	 * 
	 * @param url
	 * @param httpEntity
	 * @return
	 */
	String sendFile(HttpEntity<?> httpEntity);

	/**
	 * Скачать файл с сервера
	 * 
	 * @param url
	 *            - ссылка на рест сервис
	 * @param httpEntity
	 *            - параметры запроса
	 * @return - либо массив файла, либо сообщение ошибки
	 * @throws IOException 
	 * @throws RestClientException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	Object downloadFileFromServer(HttpEntity<?> httpEntity) throws JsonParseException, JsonMappingException, RestClientException, IOException;

	
	Object authenticate(String username, String password);

	List<FileForm> getFileFormsFromServer(HttpEntity<?> httpEntity)
			throws JsonParseException, JsonMappingException, IOException;		
}
