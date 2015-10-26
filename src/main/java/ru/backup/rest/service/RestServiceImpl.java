package ru.backup.rest.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.backup.domain.ApplicationURLs;
import ru.backup.domain.TaskForClient;
import ru.backup.domain.TaskFromServer;

/**
 * Реализация рест сервиса для доступа к серверу
 * 
 * @author Stetskevich Roman
 *
 */
@Service
public class RestServiceImpl implements RestService {
	
	/**
	 * spring рест шаблон
	 */
	private RestTemplate restTemplate;
	
	public  RestServiceImpl() {
		restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
	}

	@Override
	public String sendObject(ApplicationURLs url, HttpEntity<?> httpEntity) {
		
		return restTemplate.exchange(url.getUrl(), HttpMethod.POST, httpEntity, String.class).getBody();
	}

	@Override
	public List<TaskForClient> getTaskFromServer(ApplicationURLs url, HttpEntity<?> httpEntity) throws JsonParseException, JsonMappingException, RestClientException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		String body = restTemplate.exchange(url.getUrl(), HttpMethod.GET, httpEntity, String.class).getBody();
		
		//конвертируем тело ответа в список задач
		List<TaskForClient> readValue = mapper.readValue(body, new TypeReference<List<TaskForClient>>() {
		});
		return readValue;
	}

}
