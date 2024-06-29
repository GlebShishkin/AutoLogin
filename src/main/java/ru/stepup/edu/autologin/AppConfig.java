package ru.stepup.edu.autologin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
public class AppConfig {

    // ТЗ: "3) Промежуточная компонента проверки данных исправляет ФИО так, чтобы каждый его компонент начинался
    // с большой буквы"
    @Bean("improveFIO")
    public Processor improveFIO() {
        return new Processor(
                (str1) ->
                {
                    return Arrays.stream(str1.split("\\s"))
                            .map(word -> Character.toTitleCase(word.charAt(0)) + word.substring(1))
                            .collect(Collectors.joining(" "));
                }
        );
    }

    // ТЗ: "4) Промежуточная компонента проверяет что тип приложения соответствует одному из: “web”, “mobile”.
    // Если там записано что-либо иное, то оно преобразуется к виду “other:”+значение"
    @Bean("improveType")
    public Processor improveType() {
        return new Processor(
                (str) ->
                {
                    if ((!str.equals("web")) && (!str.equals("mobile"))){
                        return "other";
                    }
                    return str;
                }
        );
    }

    // ТЗ: "5) Промежуточная компонента проверки даты проверяет её наличие. Если дата не задана, то человек не вносится в базу,
    // а сведения о имени файла и значении человека заносятся в отдельный лог."
    @Bean("checkDate")
    public Checker checkDate() {
        return new Checker(
                (str) ->
                {
                    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z", Locale.ENGLISH);
                    sdf.setLenient(false);
                    try {
                        sdf.parse(str);
                    } catch (ParseException e) {
                        return false;
                    }
                    return true;
                }
        );
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        Properties property = new Properties();
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
            property.load(fis);
            dataSource.setUrl(property.getProperty("db.host"));
            dataSource.setUsername(property.getProperty("db.login"));
            dataSource.setPassword(property.getProperty("db.password"));

        } catch (IOException e) {
            throw new RuntimeException("ОШИБКА: Файл свойств отсуствует!");
        }
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}
