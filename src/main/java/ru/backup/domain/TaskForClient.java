package ru.backup.domain;

import java.util.List;

/**
 * 
 * Задача, которая будет передаваться клиенту.
 * 
 * @author Roman
 *
 */
public class TaskForClient {
	
	private TaskFromServer taskFromServer;
	
	private List<String> fileChecksums;

	public TaskFromServer getTaskFromServer() {
		return taskFromServer;
	}

	public void setTaskFromServer(TaskFromServer taskFromServer) {
		this.taskFromServer = taskFromServer;
	}

	public List<String> getFileChecksums() {
		return fileChecksums;
	}

	public void setFileChecksums(List<String> fileChecksums) {
		this.fileChecksums = fileChecksums;
	}
	
}
