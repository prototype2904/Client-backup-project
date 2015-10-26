package ru.backup.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import ru.backup.domain.ApplicationURLs;
import ru.backup.domain.TaskForClient;
import ru.backup.rest.service.AuthService;
import ru.backup.rest.service.AuthServiceImpl;
import ru.backup.rest.service.RestService;
import ru.backup.rest.service.RestServiceImpl;

/**
 * Сервис по работе с файлами
 * 
 * @author Stetskevich Roman
 *
 */
public class FileService {

	/**
	 * сервис для аутентификации
	 */
	private AuthService authService;

	/**
	 * рест серввис
	 */
	private RestService restService;

	public FileService() {
		this.authService = new AuthServiceImpl();
		this.restService = new RestServiceImpl();
	}

	/**
	 * Посчитать хэш файла
	 * 
	 * @param file
	 *            - файл, массив по 4 бита
	 * @return - строка хэш
	 */
	private String getHashFile(byte[] file) {

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.update(file);
			byte[] digest = messageDigest.digest();

			String checksum = new String();
			for (int i = 0; i < digest.length; i++) {
				checksum += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
			}
			return checksum;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Получить и выполнить задачи для сохранения версий файлов на сервера
	 * 
	 * @throws IOException
	 */
	public void getAndDoTasks(String username, String password) throws IOException {

		// получить задачи с сервреа
		List<TaskForClient> tasks = new ArrayList<>(
				restService.getTaskFromServer(ApplicationURLs.GET_TASKS_FROM_SERVER_URL,
						new HttpEntity<>(authService.authenticate(username, password))));

		// Выполним каждую
		tasks.forEach(taskForClient -> {

			// получим файл в виде массив по 4 бита
			byte[] file = convertFileToHex(new File(
					taskForClient.getTaskFromServer().getDirPath() + taskForClient.getTaskFromServer().getFilename()
							+ "." + taskForClient.getTaskFromServer().getFormat()));

			// посчитаем хэш файла
			String checksum = getHashFile(file);

			// посмотрим, есть ли такая версия файла уже на сервере
			if (isFileAlreadyExistOnServer(taskForClient, checksum) == false) {

				// добавим параметры запроса
				MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
				map.add("file", file); // массив файла
				map.add("filename", taskForClient.getTaskFromServer().getFilename());// название
				map.add("format", taskForClient.getTaskFromServer().getFormat());// формат
				map.add("checksum", checksum);// хэш файла

				// Создадим запрос
				HttpEntity<?> request = new HttpEntity(map, authService.authenticate("roma", "1234"));

				// отправим файл на сервер
				String response = restService.sendObject(ApplicationURLs.POST_FILE, request);

				System.out.println("File " + taskForClient.getTaskFromServer().getFilename() + "."
						+ taskForClient.getTaskFromServer().getFormat() + " was sent");
				System.out.println("Response " + response);
			} else {
				System.out.println("File " + taskForClient.getTaskFromServer().getFilename() + "."
						+ taskForClient.getTaskFromServer().getFormat() + " already exists");
			}
		});
	}

	/**
	 * Провера, есть ли файл уже на сервере
	 * 
	 * @param taskForClient
	 *            - задача, в которой есть список хэшей файлов уже хранящихся на
	 *            сервере
	 * @param hash
	 *            - хэщ нового файла
	 * @return - true - если файл уже есть и false если файл новый
	 */
	private boolean isFileAlreadyExistOnServer(TaskForClient taskForClient, String hash) {
		for (String filesHash : taskForClient.getFileChecksums()) {
			if (filesHash.equals(hash)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Перевод файла в массив по 4 бита
	 * 
	 * @param file
	 *            - файл
	 * @return массив по 4 бита
	 */
	private byte[] convertFileToHex(File file) {
		try (InputStream inputStream = new FileInputStream(file)) {
			byte[] hex = new byte[inputStream.available() * 2];
			int i = 0;
			while (inputStream.available() > 0) {
				int tmp = inputStream.read();
				hex[i++] = (byte) ((tmp >> 4) & 0x0f);
				hex[i++] = (byte) (tmp & 0x0f);
			}
			return hex;
		} catch (Exception e) {
			return new byte[0];
		}
	}
}