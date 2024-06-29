package ru.stepup.edu.autologin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.List;

@Component
public class LoginDAO {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public LoginDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // сохранение User в таблицу users
    public int save (User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int update = jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                String sql = "INSERT INTO users(username, fio) VALUES(?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql, new String[] { "id" }); // Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, user.username);
                preparedStatement.setString(2, user.fio);
                return preparedStatement;
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    // сохранение Login в таблицу logins
    public void save (Login login) {
        jdbcTemplate.update("INSERT INTO logins(user_id, access_date, application) VALUES(?, ?, ?)"
            , login.user_id, Timestamp.valueOf(login.getAccess_date()), login.application);
    }

    // выборка из таблицы Users в список
    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM users", new BeanPropertyRowMapper<>(User.class));
    }

    public User show(int id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE id = ?", new Object[]{id}
                , new BeanPropertyRowMapper<>(User.class))
                .stream().findAny().orElse(null);
    }

    public List<Login> getLogins() {
        return jdbcTemplate.query("SELECT * FROM logins", new BeanPropertyRowMapper<>(Login.class));
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from logins");
        jdbcTemplate.update("delete from users");
    }
}
