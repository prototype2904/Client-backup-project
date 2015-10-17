package ru.backup.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ru.backup.domain.ApplicationURLs;
import ru.backup.domain.TaskFromServer;

@Service
public class RestServiceImpl implements RestService {
	
	private RestTemplate restTemplate;
	
	public  RestServiceImpl() {
		restTemplate = new RestTemplate();
	}

	@Override
	public String sendObject(ApplicationURLs url, HttpEntity<?> httpEntity) {
		
		return restTemplate.postForObject(url.getUrl(), httpEntity, String.class);
	}

	@Override
	public TaskFromServer getTaskFromServer(ApplicationURLs url, HttpEntity<?> httpEntity) {
		return restTemplate.exchange(url.getUrl(), HttpMethod.GET, httpEntity, TaskFromServer.class).getBody();
	}

}
