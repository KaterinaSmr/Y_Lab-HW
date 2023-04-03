package io.ylab.intensive.lesson05.eventsourcing.db;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DbApp {
  public static void main(String[] args) throws Exception {
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
    applicationContext.registerShutdownHook();
    applicationContext.start();

    MessageScheduler messageScheduler = applicationContext.getBean(MessageScheduler.class);
    messageScheduler.start();
  }
}
