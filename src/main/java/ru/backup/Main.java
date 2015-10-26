package ru.backup;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpException;
import ru.backup.service.FileService;

public class Main {

	public static void main(String[] args)
			throws URISyntaxException, HttpException, IOException, NoSuchAlgorithmException {

		FileService fileService = new FileService();
		fileService.getAndDoTasks("roma", "1234");
		System.out.println("gwgw");

	}
}
