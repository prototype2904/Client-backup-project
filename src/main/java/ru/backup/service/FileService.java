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
	public void getAndDoTasks(String username, String password) throws IOException {

		// �������� ������ � �������
		List<TaskForClient> tasks = new ArrayList<>(
				restService.getTaskFromServer(ApplicationURLs.GET_TASKS_FROM_SERVER_URL,
						new HttpEntity<>(authService.authenticate(username, password))));

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
				HttpEntity<?> request = new HttpEntity(map, authService.authenticate("roma", "1234"));

				// �������� ���� �� ������
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
}