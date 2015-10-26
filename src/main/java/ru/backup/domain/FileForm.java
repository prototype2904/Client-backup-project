package ru.backup.domain;

import java.io.Serializable;

import org.springframework.core.io.FileSystemResource;

public class FileForm implements Serializable{

	private byte[] file;
	
	private String filename;
	
	private String format;
	
	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
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
}
