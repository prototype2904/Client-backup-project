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
 * ������ �� ������ � �������
 * 
 * @author Stetskevich Roman
 *
 */
public class FileService {

	/**
	 * ������ ��� ��������������
	 */
	private AuthService authService;

	/**
	 * ���� �������
	 */
	private RestService restService;

	public FileService() {
		this.authService = new AuthServiceImpl();
		this.restService = new RestServiceImpl();
	}

	/**
	 * ��������� ��� �����
	 * 
	 * @param file
	 *            - ����, ������ �� 4 ����
	 * @return - ������ ���
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
	 * �������� � ��������� ������ ��� ���������� ������ ������ �� �������
	 * 
	 * @throws IOException
	 */
	public void getAndDoTasks(User user) throws IOException {

		// �������� ������ � �������
		List<TaskForClient> tasks = new ArrayList<>(
				restService.getTaskFromServer(new HttpEntity<>(authService.authenticate(user.getUsername(), user.getPassword()))));

		if(tasks.size() > 0)
		{
			System.out.println("� ������� �������� " + tasks.size() + ". ���������� � ����������");
		}else{
			System.out.println("�� ������� ��� �����.");
		}
		
		// �������� ������
		tasks.forEach(taskForClient -> {

			// ������� ���� � ���� ������ �� 4 ����
			byte[] file = convertFileToHex(new File(
					taskForClient.getTaskFromServer().getDirPath() + taskForClient.getTaskFromServer().getFilename()
							+ "." + taskForClient.getTaskFromServer().getFormat()));

			// ��������� ��� �����
			String checksum = getHashFile(file);

			// ���������, ���� �� ����� ������ ����� ��� �� �������
			if (isFileAlreadyExistOnServer(taskForClient, checksum) == false) {

				// ������� ��������� �������
				MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
				map.add("file", file); // ������ �����
				map.add("filename", taskForClient.getTaskFromServer().getFilename());// ��������
				map.add("format", taskForClient.getTaskFromServer().getFormat());// ������
				map.add("checksum", checksum);// ��� �����

				// �������� ������
				HttpEntity<?> request = new HttpEntity(map, authService.authenticate(user.getUsername(), user.getPassword()));

				// �������� ���� �� ������
				String response = restService.sendFile(request);

				System.out.println("���� " + taskForClient.getTaskFromServer().getFilename() + "."
						+ taskForClient.getTaskFromServer().getFormat() + " ���������.");
				System.out.println("����� �� ������� " + response);
			} else {
				System.out.println("������ ����� " + taskForClient.getTaskFromServer().getFilename() + "."
						+ taskForClient.getTaskFromServer().getFormat() + " ��� ������� �� �������.");
			}
		});
	}
	
	public List<FileForm> geUserFileForms(User user){
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("username", user.getUsername());

		// �������� ������
		HttpEntity<?> request = new HttpEntity(map, authService.authenticate(user.getUsername(), user.getPassword()));

		// �������� ���� �� ������
		try {
			List<FileForm> response = restService.getFileFormsFromServer(request);
			return response;
		} catch (IOException e) {
			System.out.println(Errors.IO_ERROR.getMes());
		}
		return null;
	}
	
	
	

	/**
	 * �������, ���� �� ���� ��� �� �������
	 * 
	 * @param taskForClient
	 *            - ������, � ������� ���� ������ ����� ������ ��� ���������� ��
	 *            �������
	 * @param hash
	 *            - ��� ������ �����
	 * @return - true - ���� ���� ��� ���� � false ���� ���� �����
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
	 * ������� ����� � ������ �� 4 ����
	 * 
	 * @param file
	 *            - ����
	 * @return ������ �� 4 ����
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

		// ������� ��������� �������
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("filename", fileForm.getFilename());// ��������
		map.add("format", fileForm.getFormat());// ������
		map.add("version", fileForm.getVersion().toString());// ������ �����

		// �������� ������
		HttpEntity<?> request = new HttpEntity(map, authService.authenticate(fileForm.getUser().getUsername(), fileForm.getUser().getPassword()));

		// ������� ���� � �������
		Object response = null;
		try {
			response = restService.downloadFileFromServer(request);
		} catch (RestClientException | IOException e1) {
			e1.printStackTrace();
		}

		// �������� ������, ������ ������
		if (response instanceof String) {
			String error = (String) response;
			System.err.println(error);

			// ���� ������, �� ����
		} else if (response instanceof byte[]) {
			byte[] file = (byte[]) response;

			// �������� ���� ���� � ����� �������
			try {
				createFile(fileForm, file);
			} catch (IOException e) {
				System.err.println("���� ������� � �������, �� �������� �� �������. ������ �����/������.");
			}
		}
	}

	private void createFile(FileForm fileForm, byte[] file) throws IOException {
		// ������ ����� /{username}_{filename}_{version}.{format}
		Path p = Paths.get(String.format("%s/%s_.%s", ApplicationURLs.FILES_URLS.getUrl(), fileForm.getFilename(),
				fileForm.getFormat()));

		// ���� ���� ��� ����������, �� ������ ���
		if (Files.isReadable(p)) {
			deleteFile(fileForm);
		}

		// �������� ����� ����
		try (OutputStream out = new BufferedOutputStream(
				Files.newOutputStream(p, StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
			// ������� ����� ����
			// ��������� ������ � ����� 4 ���� � ���� � ���������� � ����
			for (int i = 0; i < file.length; i += 2) {
				out.write((file[i] << 4) + file[i + 1]);
			}
		} catch (IOException x) {
			throw new IOException("��������� ������� ���� ��� �������� � ����.");
		}
	}

	/**
	 * �������� �����
	 * 
	 * @param fileForm
	 *            - �������� �����
	 */
	private void deleteFile(FileForm fileForm) {

		// ������ ����� '/{username}_{filename}_{version}.{format}'
		Path p = Paths.get(String.format("%s/%s.%s", ApplicationURLs.FILES_URLS.getUrl(), fileForm.getFilename(),
				fileForm.getFormat()));

		// ������� ����
		try (OutputStream out = new BufferedOutputStream(
				Files.newOutputStream(p, StandardOpenOption.DELETE_ON_CLOSE, StandardOpenOption.APPEND))) {
		} catch (IOException x) {
			System.err.println(x);
		}
	}
}