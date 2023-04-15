package io.ylab.intensive.lesson05.eventsourcing.api;

import io.ylab.intensive.lesson05.eventsourcing.MessageMapper;
import io.ylab.intensive.lesson05.eventsourcing.Person;
import io.ylab.intensive.lesson05.eventsourcing.PersonDAO;
import io.ylab.intensive.lesson05.eventsourcing.RabbitClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * This class is responsible for interaction with data for {@link Person} objects. It can read data from database and
 * send requests for update of {@link Person} objects representation in db. <br/>
 * The requests for update are published to the RabbitMQ queue. This part is delegated to {@link RabbitClient}<br/>
 * The reading data from database is delegated to {@link PersonDAO}
 */
@Component
public class PersonApiImpl implements PersonApi {
  private final RabbitClient rabbitClient;
  private final PersonDAO personDAO;
  private final MessageMapper messageMapper;

  private final Logger logger = LoggerFactory.getLogger(PersonApiImpl.class);

  public PersonApiImpl(RabbitClient rabbitClient, PersonDAO personDAO, MessageMapper messageMapper) {
    this.rabbitClient = rabbitClient;
    this.personDAO = personDAO;
    this.messageMapper = messageMapper;
  }

  @Override
  public void deletePerson(Long personId) throws IOException {
    if (personId == null) {
      logger.warn("Failed to remove person: null is not valid for id");
      return;
    }
    rabbitClient.publish(messageMapper.wrapPersonId(personId));
  }

  @Override
  public void savePerson(Long personId, String firstName, String lastName, String middleName) throws IOException {
    if (personId == null) {
      logger.warn("Failed to save person: null is not valid for id");
      return;
    }
    Person personToSave = new Person(personId, firstName, lastName, middleName);
    rabbitClient.publish(messageMapper.wrapPerson(personToSave));
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
