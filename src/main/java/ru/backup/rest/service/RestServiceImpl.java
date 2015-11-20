package ru.backup.rest.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.backup.domain.ApplicationURLs;
import ru.backup.domain.Errors;
import ru.backup.domain.FileForm;
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

	public RestServiceImpl() {
		restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
	}

	@Override
	public String sendFile(HttpEntity<?> httpEntity) {

		try {
			return restTemplate.exchange(ApplicationURLs.POST_FILE.getUrl(), HttpMethod.POST, httpEntity, String.class)
					.getBody();
		} catch (ResourceAccessException ex) {
			System.out.println(Errors.CONNECT.getMes());
		} catch (HttpClientErrorException ex) {
			if (ex.getMessage().equals("401 Unauthorized")) {
				System.out.println(Errors.INCORRECT_LOGIN_AND_PASS.getMes());
			} else {
				System.out.println(ex.getMessage());
			}

		}
		return null;
	}

	@Override
	public List<TaskForClient> getTaskFromServer(HttpEntity<?> httpEntity)
			throws JsonParseException, JsonMappingException, IOException {
		try {
			ObjectMapper mapper = new ObjectMapper();

			String body = restTemplate.exchange(ApplicationURLs.GET_TASKS_FROM_SERVER_URL.getUrl(), HttpMethod.GET,
					httpEntity, String.class).getBody();

			// конвертируем тело ответа в список задач
			List<TaskForClient> readValue = mapper.readValue(body, new TypeReference<List<TaskForClient>>() {
			});
			return readValue;
		} catch (ResourceAccessException ex) {
			System.out.println(Errors.CONNECT.getMes());
		} catch (HttpClientErrorException ex) {
			if (ex.getMessage().equals("401 Unauthorized")) {
				System.out.println(Errors.INCORRECT_LOGIN_AND_PASS.getMes());
			} else {
				System.out.println(ex.getMessage());
			}

		}
		return null;
	}

	@Override
	public Object downloadFileFromServer(HttpEntity<?> httpEntity)
			throws JsonParseException, JsonMappingException, RestClientException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		String object = restTemplate
				.exchange(ApplicationURLs.DOWNLOAD_FILE.getUrl(), HttpMethod.POST, httpEntity, String.class).getBody();

		byte[] a = object.getBytes();
		return a;
	}

	@Override
	public Object authenticate(String username, String password) {

		AuthService authService = new AuthServiceImpl();
		try {
			ResponseEntity<Object> exchange = restTemplate.exchange(ApplicationURLs.AUTH.getUrl(), HttpMethod.GET,
					new HttpEntity(authService.authenticate(username, password)), Object.class);
			return exchange.getBody();
		} catch (ResourceAccessException ex) {
			System.out.println(Errors.CONNECT.getMes());
		} catch (HttpClientErrorException ex) {
			if (ex.getMessage().equals("401 Unauthorized")) {
				System.out.println(Errors.INCORRECT_LOGIN_AND_PASS.getMes());
			} else {
				System.out.println(ex.getMessage());
			}

		}
		return false;
	}

	@Override
	public List<FileForm> getFileFormsFromServer(HttpEntity<?> httpEntity)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		try {

			String body = restTemplate
					.exchange(ApplicationURLs.GET_FILE_FORMS.getUrl(), HttpMethod.POST, httpEntity, String.class)
					.getBody();
			// конвертируем тело ответа в список файлов
			List<FileForm> readValue = mapper.readValue(body, new TypeReference<List<FileForm>>() {
			});
			return readValue;
		} catch (ResourceAccessException ex) {
			System.out.println(Errors.CONNECT.getMes());
		} catch (HttpClientErrorException ex) {
			if (ex.getMessage().equals("401 Unauthorized")) {
				System.out.println(Errors.INCORRECT_LOGIN_AND_PASS.getMes());
			} else {
				System.out.println(ex.getMessage());
			}

		}
		return null;

	}

}
