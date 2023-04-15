package io.ylab.intensive.lesson05.messagefilter;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MessageFilterApp {
  public static void main(String[] args) throws Exception {

    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
    applicationContext.registerShutdownHook();
    applicationContext.start();

    MessageScheduler messageScheduler = applicationContext.getBean(MessageScheduler.class);
    messageScheduler.start();

  }
}
