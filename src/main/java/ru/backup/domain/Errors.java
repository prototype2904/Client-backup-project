package ru.backup.domain;

public enum Errors {
	
	NO_FILE("������ ����� � ���� ���!"),
	CANT_OPEN("�� ������� ������� ����");

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
