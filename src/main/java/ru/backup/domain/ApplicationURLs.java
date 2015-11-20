package ru.backup.domain;


/**
 * перечисление всех url - ов для приложения.
 * 
 * использовать их
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
	FILES_URLS("./"),
	AUTH("http://localhost:8080/rest/auth/"),
	GET_FILE_FORMS("http://localhost:8080/rest/tasks/files/");
	
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
