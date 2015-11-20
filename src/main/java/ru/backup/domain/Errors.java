package ru.backup.domain;

public enum Errors {
	
	NO_FILE("Такого файла в базе нет!"),
	CANT_OPEN("Не удалось открыть файл"),
	ACCESS_DENIED("Доступ запрещен! Зайдите под другим пользователем!"),
	INCORRECT_LOGIN_AND_PASS("Неверный логин или пароль. Введите снова."),
	IO_ERROR("Неверно введены данные. Попробуйте снова."),
	CONNECT("Ошибка подключения к серверу. Проверьте подключение к интернету.");

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
