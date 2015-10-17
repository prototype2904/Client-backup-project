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
	
	GET_TASKS_FROM_SERVER_URL("http://localhost:8080/rest/tasks/"),
	EXAMPLE_POST("http://localhost:8080/rest/backup/post/");
	
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
