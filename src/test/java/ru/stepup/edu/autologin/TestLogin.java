package ru.stepup.edu.autologin;

import org.junit.jupiter.api.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;

public class TestLogin {

    AnnotationConfigApplicationContext context;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext();
        context.scan("ru.stepup.edu.autologin");
        context.refresh();
    }

    @AfterEach
    public void cleanUpEach(){
        context.close();
    }

    @Test
    @DisplayName("Test parse text data and save it in database")
    void parseAndSave() {

        Model model = context.getBean(Model.class);

        // почистим все даблицы бдч
        LoginDAO loginDAO = context.getBean(LoginDAO.class);
        loginDAO.deleteAll();

        // имитируем загрузку первого файла, которая должна породить:
        // 3 записи в таблице User и 4 в таблице Logins
        ArrayList<String> lineList = new ArrayList<String>();
        lineList.add("ivanov;ivanov ivan ivanovach;26/Sep/2022:06:54:32 +0300;web");
        lineList.add("petov;petrov petr petrovich;26/Sep/2022:06:55:16 +0300;mobile");
        lineList.add("dmitriev;dmitriev dmitruy dmitrievich;26/Sep/2022:06:55:50 +0300;web");
        lineList.add("ivanov;ivanov ivan ivanovach;26/Sep/2022:06:56:56 +0300;kassa");

        model.parse("C:\\Login1.txt", lineList);	// делаем разбор строк и вставку в бд

        // проверяем, что в таблицу users вставилос 3 записи, а в logins - 4 записи
        Assertions.assertEquals(3, loginDAO.getUsers().size());
        Assertions.assertEquals(4, loginDAO.getLogins().size());

        lineList.clear();

        // имитируем загрузку второго файла, которая должна породить:
        // 1 запись в таблице User и 1 запись в таблице Logins
        // PS. Вторая строка не попадет в бд, т.к. по условию ТЗ будет отбракована и попадет в файл лога ошибок ("ErrLog")
        lineList.add("rogov;rogov sergey viktorovich;26/Sep/2023:06:55:50 +0300;kassa");
        lineList.add("simonov;simonov simon simonovich;;web");

        model.parse("C:\\Login2.txt", lineList);	// делаем разбор строк и вставку в бд

        // проверяем, что в таблицу users вставилос 4 записи, а в logins - 5 записи
        Assertions.assertEquals(4, loginDAO.getUsers().size());
        Assertions.assertEquals(5, loginDAO.getLogins().size());
    }
}
