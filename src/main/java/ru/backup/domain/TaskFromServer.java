package ru.backup.domain;

import java.util.List;


/**
 * Задача от сервера на загрузку резервной копии файлов на сервер
 * 
 * @author Roman
 *
 */
public class TaskFromServer {
	
	private String id;

	private String username;
	
	private String filePath;
	
	private String version;
	
	private String generalId;


	public String getFilePaths() {
		return filePath;
	}

	public void setFilePaths(String filePath) {
		this.filePath = filePath;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGeneralId() {
		return generalId;
	}

	public void setGeneralId(String generalId) {
		this.generalId = generalId;
	}
}
