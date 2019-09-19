package com.github.fantasticlab.rpc.core.annotation;

import com.github.fantasticlab.rpc.core.provider.ServiceRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public class FrpcLoader {

    private ServiceRegistry serviceRegistry;

    private ApplicationContext context;

    private XmlBeanFactory xmlBeanFactory;

    public FrpcLoader(ServiceRegistry serviceRegistry, ApplicationContext context) {
        this.serviceRegistry = serviceRegistry;
        this.context = context;
    }

    public FrpcLoader(ServiceRegistry serviceRegistry, XmlBeanFactory xmlBeanFactory) {
        this.serviceRegistry = serviceRegistry;
        this.xmlBeanFactory = xmlBeanFactory;
    }

    public void scan() throws BeansException {
//        Map<String, Object> beans = context.getBeansWithAnnotation(Frpc.class);
        Map<String, Object> beans;
        if (xmlBeanFactory != null) {
            beans = xmlBeanFactory.getBeansWithAnnotation(Frpc.class);
        } else {
            beans = context.getBeansWithAnnotation(Frpc.class);
        }
        for (Object bean : beans.values()) {
            Class<?> clazz = bean.getClass();
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> inter : interfaces){
                serviceRegistry.register(inter);
            }
        }
    }

}
