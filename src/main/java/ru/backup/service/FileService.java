package ru.backup.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import ru.backup.domain.ApplicationURLs;
import ru.backup.domain.Errors;
import ru.backup.domain.FileForm;
import ru.backup.domain.TaskForClient;
import ru.backup.domain.User;
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
	public void getAndDoTasks(User user) throws IOException {

		// получить задачи с сервреа
		List<TaskForClient> tasks = new ArrayList<>(
				restService.getTaskFromServer(new HttpEntity<>(authService.authenticate(user.getUsername(), user.getPassword()))));

		if(tasks.size() > 0)
		{
			System.out.println("С сервера получено " + tasks.size() + ". Приступаем к выполнению");
		}else{
			System.out.println("На сервере нет задач.");
		}
		
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
				HttpEntity<?> request = new HttpEntity(map, authService.authenticate(user.getUsername(), user.getPassword()));

				// отправим файл на сервер
				String response = restService.sendFile(request);

				System.out.println("Файл " + taskForClient.getTaskFromServer().getFilename() + "."
						+ taskForClient.getTaskFromServer().getFormat() + " отправлен.");
				System.out.println("Ответ от сервера " + response);
			} else {
				System.out.println("Версия файла " + taskForClient.getTaskFromServer().getFilename() + "."
						+ taskForClient.getTaskFromServer().getFormat() + " уже имеется на сервере.");
			}
		});
	}
	
	public List<FileForm> geUserFileForms(User user){
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("username", user.getUsername());

		// Создадим запрос
		HttpEntity<?> request = new HttpEntity(map, authService.authenticate(user.getUsername(), user.getPassword()));

		// отправим файл на сервер
		try {
			List<FileForm> response = restService.getFileFormsFromServer(request);
			return response;
		} catch (IOException e) {
			System.out.println(Errors.IO_ERROR.getMes());
		}
		return null;
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

	public void downloadFileFromServer(FileForm fileForm) {

		// добавим параметры запроса
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("filename", fileForm.getFilename());// название
		map.add("format", fileForm.getFormat());// формат
		map.add("version", fileForm.getVersion().toString());// версия файла

		// Создадим запрос
		HttpEntity<?> request = new HttpEntity(map, authService.authenticate(fileForm.getUser().getUsername(), fileForm.getUser().getPassword()));

		// получим файл с сервера
		Object response = null;
		try {
			response = restService.downloadFileFromServer(request);
		} catch (RestClientException | IOException e1) {
			e1.printStackTrace();
		}

		// получили строку, значит ошибка
		if (response instanceof String) {
			String error = (String) response;
			System.err.println(error);

			// если массив, то файл
		} else if (response instanceof byte[]) {
			byte[] file = (byte[]) response;

			// создадим этот файл в нашей системе
			try {
				createFile(fileForm, file);
			} catch (IOException e) {
				System.err.println("Файл получен с сервера, но записать не удалось. Ошибка ввода/вывода.");
			}
		}
	}

	private void createFile(FileForm fileForm, byte[] file) throws IOException {
		// формат файла /{username}_{filename}_{version}.{format}
		Path p = Paths.get(String.format("%s/%s_.%s", ApplicationURLs.FILES_URLS.getUrl(), fileForm.getFilename(),
				fileForm.getFormat()));

		// если файл уже существует, то удалим его
		if (Files.isReadable(p)) {
			deleteFile(fileForm);
		}

		// создадим новый файл
		try (OutputStream out = new BufferedOutputStream(
				Files.newOutputStream(p, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
			// запишем новый файл
			// склеиваем правые и левые 4 бита в байт и записываем в файл
			for (int i = 0; i < file.length; i += 2) {
				out.write((file[i] << 4) + file[i + 1]);
			}
		} catch (IOException x) {
			throw new IOException("Неудалось открыть файл или записать в него.");
		}
	}

	/**
	 * удаление файла
	 * 
	 * @param fileForm
	 *            - описание файла
	 */
	private void deleteFile(FileForm fileForm) {

		// формат файла '/{username}_{filename}_{version}.{format}'
		Path p = Paths.get(String.format("%s/%s.%s", ApplicationURLs.FILES_URLS.getUrl(), fileForm.getFilename(),
				fileForm.getFormat()));

		// удалить файл
		try (OutputStream out = new BufferedOutputStream(
				Files.newOutputStream(p, StandardOpenOption.DELETE_ON_CLOSE, StandardOpenOption.APPEND))) {
		} catch (IOException x) {
			System.err.println(x);
		}
	}
}