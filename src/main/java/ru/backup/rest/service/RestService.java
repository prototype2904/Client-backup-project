package ru.backup.rest.service;

import org.springframework.http.HttpEntity;

import ru.backup.domain.ApplicationURLs;
import ru.backup.domain.TaskFromServer;

/**
 * ������, ��� ���� �������� �� ������
 * @author Roman
 *
 */
public interface RestService {

	/**
	 * �������� ������ � �������
	 * @param url
	 * @param httpEntity
	 * @return
	 */
	TaskFromServer getTaskFromServer(ApplicationURLs url, HttpEntity<?> httpEntity);

	/**
	 * ��������� ������ �� ������
	 * @param url
	 * @param httpEntity
	 * @return
	 */
	public String sendObject(ApplicationURLs url, HttpEntity<?> httpEntity);


}
