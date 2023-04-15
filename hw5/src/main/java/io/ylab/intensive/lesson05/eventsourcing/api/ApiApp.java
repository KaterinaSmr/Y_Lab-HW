package io.ylab.intensive.lesson05.eventsourcing.api;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApiApp {
  public static void main(String[] args) throws Exception {
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
    applicationContext.registerShutdownHook();
    applicationContext.start();

    PersonApi personApi = applicationContext.getBean(PersonApi.class);
    personApi.savePerson(1L, "Steve", "Wozniak", "Gary ");
    personApi.savePerson(2L, "Elon", "Musk", "Reeve");
    personApi.savePerson(3L, "Nikola", "Tesla", "---");
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println(personApi.findAll());

    //Изменение
    personApi.savePerson(1L, "Steve", "Jobs", "Paul");

    //Удаление Person по personId
    personApi.deletePerson(3L);
    personApi.deletePerson(55L);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    //Получение из базы
    System.out.println(personApi.findAll());
    System.out.println("Person with person id = 3 " + personApi.findPerson(3L));
    System.out.println("Person with person id = 1 " + personApi.findPerson(1L));

  }
}
