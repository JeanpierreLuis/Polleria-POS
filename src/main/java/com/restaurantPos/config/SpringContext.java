package com.restaurantPos.config;

import org.springframework.context.ApplicationContext;

public class SpringContext {

    private static ApplicationContext context;

    public static void setContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }
}