package com.example.stadiumreservation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig {

    // 1. Standart dili təyin edirik (Məsələn: Azərbaycan dili - "az")
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(new Locale("az"));
        return resolver;
    }

    // 2. Mesaj fayllarının (messages_az.properties, messages_ru.properties) harada olduğunu göstəririk
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages"); // resources/messages.properties fayllarına baxacaq
        source.setDefaultEncoding("UTF-8"); // Azərbaycan və Rus hərflərinin düzgün görünməsi üçün UTF-8
        return source;
    }
}
