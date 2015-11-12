package ru.backup;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpException;

import ru.backup.domain.FileForm;
import ru.backup.domain.User;
import ru.backup.service.FileService;

public class Main {

	public static void main(String[] args)
			throws URISyntaxException, HttpException, IOException, NoSuchAlgorithmException {

		User user = new User();
		user.setUsername("roma");
		user.setPassword("1234");
		FileService fileService = new FileService();
		fileService.getAndDoTasks(user);
		System.out.println("gwgw");
		
		fileService.downloadFileFromServer(new FileForm("Tede3uaDEgw", "jpg", user, 1L));

	}
}
