package io.ylab.intensive.lesson04.eventsourcing.api;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.ylab.intensive.lesson04.DbUtil;
import io.ylab.intensive.lesson04.RabbitMQUtil;

import javax.sql.DataSource;
import java.sql.SQLException;

public class ApiApp {
  public static void main(String[] args) throws Exception {
    DataSource dataSource = initDb();
    ConnectionFactory connectionFactory = initMQ();

    /* Из того, что я читала, не рекомендуетя создавать отдельный connection + channel к RabbitMQ для каждой публикации,
       a также не рекомендуется создавать несколько каналов в рамках connection.
       Поэтому создаем connection + channel здесь и передаем их в PersonApi

       Еще вопрос - держать ли открытым подключение к БД или открывать/закрывать соеденинение под каждый запрос.
       Я к сожалению не нашла однозначного ответа, вероятно это зависит от множества параметров - частоты обращения
       к БД, загруженности БД и т.д. Для простоты считаем, что нас устраивает держать подключение к БД открытым здесь (и в DbApp)
       в течение всего времени жизни приложения.

       Также делаем предположение, что DDL запросы в БД выполняются DbApp, и что все таблицы уже созданы, т.е здесь мы не
       создаем таблиц чтобы избежать перезатирания, а просто подключаемся к базе
     */
    try (Connection connection = connectionFactory.newConnection();
         Channel channel = connection.createChannel();
         java.sql.Connection dbConnection = dataSource.getConnection()) {
      PersonApi personApi = new PersonApiImpl(dbConnection, channel);

      //Добавление Person
      personApi.savePerson(1L, "Steve", "Jobs", "Paul");
      personApi.savePerson(2L, "Elon", "Musk", "Reeve");
      personApi.savePerson(3L, "Nikola", "Tesla", "---");
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println(personApi.findAll());

      //Удаление Person по personId
      personApi.savePerson(null, "Sergey", "Brin", "Mikhailovich");
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

  private static DataSource initDb() throws SQLException {
    return DbUtil.buildDataSource();
  }

  private static ConnectionFactory initMQ() throws Exception {
    return RabbitMQUtil.buildConnectionFactory();
  }
}
