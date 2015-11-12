package ru.backup.domain;

import java.io.Serializable;


/**
 * 
 * 
 * @author Stetskevich Roman
 *
 */
public class FileForm implements Serializable {

	private Long id;

	private String filename;

	private String format;

	private User user;

	private Long version;
	
	private String checksum;
	
	public FileForm() {
	}

	public FileForm(String filename, String format, String checksum) {
		this.filename = filename;
		this.format = format;
		this.checksum = checksum;
	}

	public FileForm(String filename, String format, User user, Long version) {
		super();
		this.filename = filename;
		this.format = format;
		this.user = user;
		this.version = version;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
}
