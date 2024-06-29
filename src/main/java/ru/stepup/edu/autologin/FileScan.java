package ru.stepup.edu.autologin;

import org.springframework.stereotype.Component;
import java.io.*;
import java.util.ArrayList;

// ТЗ: "1) Компонента чтения данных получает адрес папки файловой системы,
// сканирует имеющиеся там файлы и возвращает прочитанные строки"
@Component
public class FileScan {

    public ArrayList<String> read(String path) {

        File file = new File(path);
        boolean fileExists = file.exists();
        boolean isDirectory = file.isDirectory();

        if (fileExists && !isDirectory) {
            System.out.println("Путь указан верно");
        } else if (!fileExists) {
            System.out.println("файл не существует");
            return null;
        } else if (isDirectory) {
            System.out.println("указанный путь является путём к папке");
            return null;
        }

        ArrayList<String> list = new ArrayList<String>();

        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader reader =
                    new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            return null;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return list;
    }
}
