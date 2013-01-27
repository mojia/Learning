package com.m.server.restlet.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerMain {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] { "applicationContext-resources.xml",
                        "applicationContext-router.xml",
                        "applicationContext-server.xml" });
        
        org.restlet.Component component = (org.restlet.Component) context.getBean("top");
        try {
            component.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
