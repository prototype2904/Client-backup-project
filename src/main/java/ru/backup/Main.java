package ru.backup;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpException;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import ru.backup.domain.ApplicationURLs;
import ru.backup.domain.TaskFromServer;
import ru.backup.rest.service.AuthService;
import ru.backup.rest.service.AuthServiceImpl;
import ru.backup.rest.service.RestService;
import ru.backup.rest.service.RestServiceImpl;


public class Main {
	
	public static void main(String[] args) throws URISyntaxException, HttpException, IOException {
		//сервис для нужного представления логина и пароля
		AuthService authService = new AuthServiceImpl();
		
		//сервис, в котором выполняются все рест запросы
		RestService restService = new RestServiceImpl();
		
		String url = "http://localhost:8080/rest/backup/get/";
		
		//объект который отсылаем на сервер
		//для списка объектов использовать MultiValueMap
		TaskFromServer objectToPost = new TaskFromServer();
		
		HttpEntity<?> request = new HttpEntity(objectToPost, authService.authenticate("roma", "1234"));
		
		//выполняем пост и получаем на выходе 
		String sendObject = restService.sendObject(ApplicationURLs.EXAMPLE_POST, request);
		
		
	}
}
