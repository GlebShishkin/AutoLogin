package ru.stepup.edu.autologin;

import ru.stepup.edu.autologin.annotation.LogTransformation;

import java.util.function.Predicate;

public class Checker {
    private Predicate<String> predicate = null;

    public Checker(Predicate<String>  predicate) {
        this.predicate = predicate;
    }
    @LogTransformation
    public boolean check(String str) {
        return predicate.test(str);
    }
}
