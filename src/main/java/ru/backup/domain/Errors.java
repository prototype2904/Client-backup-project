package ru.backup.domain;

public enum Errors {
	
	NO_FILE("������ ����� � ���� ���!"),
	CANT_OPEN("�� ������� ������� ����"),
	ACCESS_DENIED("������ ��������! ������� ��� ������ �������������!"),
	INCORRECT_LOGIN_AND_PASS("�������� ����� ��� ������. ������� �����."),
	IO_ERROR("������� ������� ������. ���������� �����."),
	CONNECT("������ ����������� � �������. ��������� ����������� � ���������.");

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
