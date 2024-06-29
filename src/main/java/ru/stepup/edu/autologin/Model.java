package ru.stepup.edu.autologin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Component
public class Model {

    @Autowired
    @Qualifier("improveFIO")
    Processor improveFIO;   // ТЗ: "3) Промежуточная компонента проверки данных исправляет ФИО так, чтобы каждый его компонент начинался с большой буквы"

    @Autowired
    @Qualifier("improveType")
    Processor improveType;  // ТЗ: "4) Промежуточная компонента проверяет что тип приложения соответствует одному из: “web”, “mobile”. Если там записано что-либо иное, то оно преобразуется к виду “other:”+значение"

    @Autowired
    @Qualifier("checkDate")
    Checker checkDate;  // ТЗ "5) Промежуточная компонента проверки даты проверяет её наличие. Если дата не задана, то человек не вносится в базу, а сведения о имени файла и значении человека заносятся в отдельный лог."

    @Autowired
    LoginDAO loginDAO;

    HashMap<String, Integer> usersMap = new HashMap<>(); // Map для хранения в памяти логин/id из таблице Users
    static final private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z", Locale.ENGLISH);
    private static SimpleFormatter formatter = new SimpleFormatter();   // формат логирования в файл

    public void parse(String fileName, ArrayList<String> lineList) {

        // прочитаем всех пользователей из таблицы users бд и сложим их в Map, где ключ - username, а значение - "id" primary key таблицы users
        List<User> userList = loginDAO.getUsers();
        for (User user:userList) {
            usersMap.put(user.getUsername(), user.getId());
        }

        // обрабатываем список строк "lineList" полученный из парсера "FileScan"
        for (String strLine:lineList) {

            List<String> items = Arrays.asList(strLine.split(";")); // список полей в строке: username, fio, access_date, applecation
            int user_id;

            // ТЗ: "5) Промежуточная компонента проверки даты проверяет её наличие. Если дата не задана,
            // то человек не вносится в базу, а сведения о имени файла и значении человека заносятся в отдельный лог."
            if (checkDate.check(items.get(2))) {

                // Таблица users: 0-поле "username" + 1-поле "fio"
                if (usersMap.containsKey(items.get(0))) {
                    user_id = usersMap.get(items.get(0));   // такой User уже есть в бд - возьмем его id из Map
                }
                else {
                    // в бд нет user-а "username" -> вставим его в таблицу users и в Map
                    User user = new User(items.get(0), improveFIO.process(items.get(1)));   // username + fio
                    user_id = loginDAO.save(user);
                    // сохраним нового user-а в Map
                    usersMap.put(user.getUsername(), user_id);
                }

                // Таблица Logins: 2-поле "access_date" + 3-поле "applecation"
                try {
                    LocalDateTime ldt = DATE_FORMAT.parse(items.get(2)).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    Login login = new Login(user_id
                            , ldt
                            , improveType.process(items.get(3)) // ТЗ: "4) Промежуточная компонента проверяет что тип приложения соответствует одному из: “web”, “mobile”. Если там записано что-либо иное, то оно преобразуется к виду “other:”+значение."
                    );
                    loginDAO.save(login);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                };
            }
            else {
                // ТЗ: "5) Промежуточная компонента проверки даты проверяет её наличие. Если дата не задана,
                // то человек не вносится в базу, а сведения о имени файла и значении человека заносятся в отдельный лог."
                Logger logger = Logger.getLogger("ErrLog");
                try {
                    FileHandler fh = new FileHandler(System.getProperty("user.dir") + "\\ErrLog");
                    logger.addHandler(fh);
                    fh.setFormatter(formatter); // простой текстовый формат логирования

                    // ТЗ: "Если дата не задана, то человек не вносится в базу, а сведения о имени файла и значении человека заносятся в отдельный лог"
                    logger.info("fileName = " + fileName + "; username = " + items.get(0));
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }   // обрабатываем список строк "lineList" полученный из парсера "FileScan"
    }
}
