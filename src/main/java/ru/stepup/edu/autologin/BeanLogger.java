package ru.stepup.edu.autologin;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import ru.stepup.edu.autologin.annotation.LogTransformation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// ТЗ: " Часть 2. Логирование
//Разработайте аннотацию @LogTransformation, которая может быть использована для аннотирования промежуточных компонент системы.
// Если промежуточный компонент имеет такую аннотацию, то после завершения выполнения операции в лог записывается дата-время
// начала операции, название класса компоненты, данные полученные на вход операции, данные возвращаемые в результате операции.
// Название лог файла может быть указано в качестве параметра аннотации."

// прокси-класс для перехвата вызовов методов бинов
@Component
public class BeanLogger implements BeanPostProcessor {
    private static SimpleFormatter formatter = new SimpleFormatter();   // формат логирования в файл

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        // создаем прокси оболочку для бинов, методы которых имеют аннотацию "LogTransformation"
        if (Arrays.stream(bean.getClass()
                        .getMethods())
                .filter(m -> m.isAnnotationPresent(LogTransformation.class)).count() > 0) {
            ProxyFactory proxyFactory = new ProxyFactory(bean);
            proxyFactory.addAdvice(new LoggingInterceptor());
            return proxyFactory.getProxy();
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    // прокси оболочка для вызовов методов бинов
    private static class LoggingInterceptor implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {

            Object returnValue = methodInvocation.proceed();

            // Логирование вызова операции аннотируемой "@LogTransformation".
            // PS. В качестве логирования используется java.util.logging.Logger
            if (methodInvocation.getMethod().isAnnotationPresent(LogTransformation.class)) {

                Logger logger = Logger.getLogger("MyLog");
                try {
                    FileHandler fh = new FileHandler(System.getProperty("user.dir") + "\\Log");
                    logger.addHandler(fh);
                    fh.setFormatter(formatter); // простой текстовый формат логирования

                    // ТЗ В лог записывается: дата-время, название класса, данные полученные на вход операции, данные возвращаемые в результате операции
                    logger.info("дата-время: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                            + ": класс: " + methodInvocation.getThis().getClass().getName()
                            + "; метод: " + methodInvocation.getMethod().getName());
                    logger.info("входящие данные:");
                    for (int x = 0; x < methodInvocation.getArguments().length; x++) {
                        logger.info((String) methodInvocation.getArguments()[x]);
                    }
                    logger.info("возвращаемые данные: " + (returnValue == null ? "" : returnValue.toString()));

                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return returnValue;
        }
    }
}
