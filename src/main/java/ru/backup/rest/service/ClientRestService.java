package ru.backup.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ru.backup.domain.TaskFromServer;

public class ClientRestService {
	
	private RestTemplate restTemplate;
	
	@Autowired
	private AuthService authService;
	
	public ClientRestService()
	{
		this.restTemplate = new RestTemplate();
	}
	
	public ResponseEntity<String> sendFile()
	{
		return null;
		///
	}
	
	public List<TaskFromServer> getTasksFromServer()
	{
		return null;
	}

}
