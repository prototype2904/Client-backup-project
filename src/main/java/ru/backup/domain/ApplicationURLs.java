package ru.backup.domain;


/**
 * ������������ ���� url - �� ��� ����������.
 * 
 * ������������ ��
 *  
 * @author Roman
 *
 */
public enum ApplicationURLs {
	
	GET_TASKS_FROM_SERVER_URL("http://localhost:8080/rest/tasks/all/"),
	CORRECT_FILE("http://localhost:8080/rest/files/correct/"),
	POST_FILE("http://localhost:8080/rest/files/upload/"),
	EXAMPLE_POST("http://localhost:8080/rest/backup/post/"),
	DOWNLOAD_FILE("http://localhost:8080/rest/files/download/"),
	FILES_URLS("./");
	
	private String url;
	
	private ApplicationURLs(String url)
	{
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	

}
