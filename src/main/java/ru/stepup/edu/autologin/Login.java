package ru.stepup.edu.autologin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class Login {
    @Getter @Setter
    LocalDateTime access_date;
    @Getter @Setter
    String application;
    @Getter @Setter
    Integer user_id;

    public Login() {
    }

    public Login(Integer user_id, LocalDateTime access_date, String application) {
        this.user_id = user_id;
        this.access_date = access_date;
        this.application = application;
    }
}