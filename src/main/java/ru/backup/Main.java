package ru.backup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class Main {
	
	

	public static void main(String[] args) throws URISyntaxException, HttpException, IOException {
		
		RestTemplate resttemplate = new RestTemplate();
		
		String url = "http://localhost:8080/rest/backup/get/";
		
		
		
		String get = resttemplate.getForObject(url, String.class);

		System.out.println(get.toString());

	}
}
