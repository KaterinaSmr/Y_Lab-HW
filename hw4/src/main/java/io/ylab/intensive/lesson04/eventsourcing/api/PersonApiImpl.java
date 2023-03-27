package io.ylab.intensive.lesson04.eventsourcing.api;

import com.rabbitmq.client.Channel;
import io.ylab.intensive.lesson04.eventsourcing.Person;
import io.ylab.intensive.lesson04.eventsourcing.PersonDAO;
import io.ylab.intensive.lesson04.eventsourcing.PersonDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * This class is responsible for interaction with data for {@link Person} objects. It can read data from database and
 * send requests for update of {@link Person} objects representation in db. <br/>
 * The requests for update are published to the RabbitMQ queue. This part is delegated to {@link PersonPublisher}<br/>
 * The reading data from database is delegated to {@link PersonDAO}
 */
public class PersonApiImpl implements PersonApi {
  private final PersonPublisher personPublisher;
  private final PersonDAO personDAO;
  private final Logger logger = LoggerFactory.getLogger(PersonApiImpl.class);

  public PersonApiImpl(Connection connection, Channel channel) throws IOException {
    this.personPublisher = new PersonPublisherImpl(channel);
    this.personDAO = new PersonDAOImpl(connection);
  }

  @Override
  public void deletePerson(Long personId) throws IOException {
    if (personId == null) {
      logger.warn("Failed to remove person: null is not valid for id");
      return;
    }
    personPublisher.removePerson(personId);
  }

  @Override
  public void savePerson(Long personId, String firstName, String lastName, String middleName) throws IOException {
    if (personId == null) {
      logger.warn("Failed to save person: null is not valid for id");
      return;
    }
    Person personToSave = new Person(personId, firstName, lastName, middleName);
    personPublisher.savePerson(personToSave);
  }

  @Override
  public Person findPerson(Long personId) throws SQLException {
    if (personId == null) {
      logger.warn("Failed to find person: null is not valid for id");
      return null;
    }
    return personDAO.findPerson(personId);
  }

  @Override
  public List<Person> findAll() throws SQLException {
    return personDAO.findAll();
  }
}
