package ru.stepup.edu.autologin;

import ru.stepup.edu.autologin.annotation.LogTransformation;

import java.util.function.Function;

public class Processor {
    private Function<String, String> function = null;

    public Processor(Function<String, String> function) {
        this.function = function;
    }

    @LogTransformation
    public String process(String str) {
        return function.apply(str);
    }
}
