package ru.backup.domain;

public enum Errors {
	
	NO_FILE("Такого файла в базе нет!"),
	CANT_OPEN("Не удалось открыть файл");

	private String mes;
	
	private Errors(String mes)
	{
		this.mes = mes;
	}
	
	public String getMes()
	{
		return mes;
	}
}
