package ru.backup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpException;

import ru.backup.domain.Errors;
import ru.backup.domain.FileForm;
import ru.backup.domain.User;
import ru.backup.rest.service.AuthService;
import ru.backup.rest.service.AuthServiceImpl;
import ru.backup.rest.service.RestService;
import ru.backup.rest.service.RestServiceImpl;
import ru.backup.service.FileService;

public class Main {

	private final static String WELCOME = "Добро пожаловать в систему резервного копирования!";

	private final static String HELLO = "Приветствую, ";

	private final static String INPUT_LOGIN_AND_PASS = "Для входа в систему необходимо ввести логин и пароль";

	private final static String ON_BACKUP_MODE = "Включить режим резервного копирования.";

	private final static String FILES_ON_SERVER = "Посмотреть, какие файлы есть на сервере";

	private final static String DOWNLOAD_VER_FILE = "Скачать версию одного файла";

	private final static String DOWNLOAD_ALL_VERS = "Скачать все версии одного файла";

	private final static String CHANGE_USER = "Сменить пользователя";

	private final static String MES_BACKUP_MODE = "Каждые 20 секунд будут получаться задачи с сервера для резервгого копирования. \n Для выхода введите 0\n";

	private final static String BEGIN_BACKUP = "Началось резеврное копирование:";

	private final static int timer_sec = 5 * 1000;

	private static User user = new User();

	private static FileService fileService = new FileService();

	private static AuthService authService = new AuthServiceImpl();

	private static String str = "";

	private static int stopBackup = 1;

	private static Scanner sc = new Scanner(System.in);

	private static String menu() {
		if (user == null || user.getUsername() == null) {
			return null;
		}
		String menu = HELLO + user.getUsername() + "1. " + ON_BACKUP_MODE + "\n" + "2. " + FILES_ON_SERVER + "\n"
				+ "3. " + DOWNLOAD_VER_FILE + "\n" + "4. " + DOWNLOAD_ALL_VERS + "\n" + "5. " + CHANGE_USER + "\n";
		return menu;
	}

	private static void selectMode() {
		try{
			stopBackup = 1;
			System.out.println(menu());
			int i = 0;
			String nextLine = sc.nextLine();
			try {
	
				i = Integer.parseInt(nextLine);
			} catch (NumberFormatException exception) {
				System.out.println(Errors.IO_ERROR.getMes());
				selectMode();
			}
			switch (nextLine) {
			case "1":
				onBackupMode();
				break;
			case "2":
				showAllFilesOnServer();
				break;
			default:
				System.out.println(Errors.IO_ERROR);
				selectMode();
				break;
			}
		}
		catch(IndexOutOfBoundsException ex){
			
		}
	}

	private static void showAllFilesOnServer() {
		if (user == null || user.getUsername() == null) {
			System.out.println("Введите логин и пароль");
			inputUsernameAndPassword();
			return;
		} else {
			List<FileForm> forms = fileService.geUserFileForms(user);

			System.out.println("0 - Выход");
			if (forms.size() > 0) {
				for (int i = 0; i < forms.size(); i++) {
					System.out.println(i + 1 + ". " + forms.get(i).getFilename() + "." + forms.get(i).getFormat()
							+ " (версия " + forms.get(i).getVersion() + ")");
				}
				try {
					int number = Integer.parseInt(sc.nextLine());
					if (number == 0) {
						selectMode();
					} else if (number >= 1 && number <= forms.size()) {
						FileForm fileForm = forms.get(number - 1);
						fileService.downloadFileFromServer(fileForm);
						System.out.println("Файл удачно скачан.");
						selectMode();
					} else {
						System.out.println(Errors.IO_ERROR.getMes());
						showAllFilesOnServer();
					}
				} catch (NumberFormatException ex) {
					System.out.println(Errors.IO_ERROR.getMes());
					showAllFilesOnServer();
				}
			} else {
				System.out.println("Файлов на сервере нет. Для выхода введите любой символ");
				int number = sc.nextInt();
				selectMode();
			}
		}

	}

	private static void close(Scanner scanner) {
		while (scanner.hasNext()) {
			scanner.next();
		}
		scanner.close();
	}

	private static void auth(String username, String password) {
		RestService restService = new RestServiceImpl();
		boolean authenticate = (boolean) restService.authenticate(username, password);
		user.setUsername(username);
		user.setPassword(password);
		if (authenticate) {
			selectMode();
		} else {
			inputUsernameAndPassword();
		}

	}

	private static void inputUsernameAndPassword() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
			System.out.println(INPUT_LOGIN_AND_PASS);
			String username = "";
			String password = "";
			System.out.println("Логин:");
			username = sc.nextLine();
			System.out.println("Пароль:");
			password = sc.nextLine();
			auth(username, password);
		} catch (IOException e) {
			System.out.println(Errors.IO_ERROR.getMes());
			inputUsernameAndPassword();
		}

	}

	private static void onBackupMode() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				if (stopBackup == 1) {
					System.out.println(BEGIN_BACKUP);

					try {
						fileService.getAndDoTasks(user);
						selectMode();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					selectMode();
				}
			}
		}, timer_sec);

		System.out.println(MES_BACKUP_MODE);
		try {
			stopBackup = Integer.parseInt(sc.nextLine());
		} catch (NumberFormatException ex) {
			System.out.println(Errors.IO_ERROR.getMes());
		}
		if (stopBackup == 0) {
			timer.cancel();
			selectMode();
		} else {
			timer.cancel();
			System.out.println(Errors.IO_ERROR.getMes());
			onBackupMode();
		}
		timer.cancel();
	}

	private static void start() {
		System.out.println(WELCOME);
		if (user.getUsername() == null) {
			inputUsernameAndPassword();
		} else {
			selectMode();
		}
	}

	public static void main(String[] args)
			throws URISyntaxException, HttpException, IOException, NoSuchAlgorithmException {

		start();

		// User user = new User();
		// user.setUsername("roma");
		// user.setPassword("1234");
		//
		// List<FileForm> geUserFileForms = fileService.geUserFileForms(user);
		// for (FileForm fileForm : geUserFileForms) {
		// System.out.println(fileForm.getFilename());
		// }
		//
		// fileService.getAndDoTasks(user);
		// System.out.println("gwgw");
		//
		// fileService.downloadFileFromServer(new FileForm("Tede3uaDEgw", "jpg",
		// user, 1L));
		//
	}
}

// import java.util.Timer;
// import java.util.TimerTask;
// import java.io.*;
//
// public class Main {
// private String str = "";
//
// TimerTask task = new TimerTask() {
// public void run() {
// if (str.equals("")) {
// System.out.println("you input nothing. exit...");
// System.exit(0);
// }
// }
// };
//
// public void getInput() throws Exception {
// Timer timer = new Timer();
// timer.schedule(task, 10 * 1000);
//
// System.out.println("Input a string within 10 seconds: ");
// BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
// str = in.readLine();
//
// timer.cancel();
// System.out.println("you have entered: " + str);
// }
//
// public static void main(String[] args) {
// try {
// (new Main()).getInput();
// } catch (Exception e) {
// System.out.println(e);
// }
// System.out.println("main exit...");
// }
// }