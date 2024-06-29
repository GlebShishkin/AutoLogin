package ru.stepup.edu.autologin;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class User {

    @Getter @Setter
    Integer id;

    @Getter @Setter
    @NotEmpty(message = "Имя пользователя не может быть пустым")
            @Size(min = 2, max = 30, message = "Длина имени должно быть от 2 до 30")
    String username;
    @Getter @Setter
    @NotEmpty(message = "ФИО пользователя не может быть пустым")
    String fio;

    public User() {
    }

    public User(String username, String fio) {
        this.username = username;
        this.fio = fio;
    }

    public User(Integer id, String username, String fio) {
        this.id = id;
        this.username = username;
        this.fio = fio;
    }
}
