package ru.stepup.edu.autologin;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.Scanner;

@SpringBootApplication
public class AutoLoginApplication {

	public static void main(String[] args) {

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("ru.stepup.edu.autologin");
		context.refresh();

		FileScan fileScan = context.getBean(FileScan.class);	// ТЗ: "1) Компонента чтения данных..."

		System.out.println("Укажите путь к файлу загрузки данных:");
		String path = new Scanner(System.in).nextLine();

		// ТЗ: "1) Компонента чтения данных получает адрес папки файловой системы, сканирует имеющиеся там файлы и возвращает прочитанные строки"
		ArrayList<String> lineList = fileScan.read(path);

		// Model вызывает промежуточные компоненты (указанные в ТЗ) + компоненту записи данных
		Model model = context.getBean(Model.class);
		model.parse(path, lineList);	// делаем разбор и обработку строк и вставку в бд

		context.close();
	}
}
