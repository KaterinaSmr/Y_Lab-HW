package io.ylab.intensive.lesson05.eventsourcing.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.ylab.intensive.lesson05.eventsourcing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This class is responsible for receiving and processing messages from RabbitMQ.
 * The outcome of a message processing should be an update to the db. The message parsing is delegated to a
 * {@link MessageMapper} object. The update to the db is delegated to a {@link PersonDAO} object.
 * {@link RabbitClient} is responsible for getting message.
 */
@Component
public class MessageProcessorImpl implements MessageProcessor {
  private final RabbitClient rabbitClient;
  private final PersonDAO personDAO;
  private final MessageMapper messageMapper;
  private final Logger logger = LoggerFactory.getLogger(MessageProcessorImpl.class);

  public MessageProcessorImpl(RabbitClient rabbitClient, PersonDAO personDAO, MessageMapper messageMapper) {
    this.rabbitClient = rabbitClient;
    this.personDAO = personDAO;
    this.messageMapper = messageMapper;
  }

  /**
   * Retrieves single message from the RabbitMQ queue. Parses the message type with the {@link MessageMapper}
   * and posts updates to the database with the {@link PersonDAO} depending on the message type.
   * @throws SQLException
   * @throws IOException
   */
  @Override
  public void process() throws SQLException, IOException {
    String messageReceived = rabbitClient.consume();
    if (messageReceived == null) {
      return;
    }
    try {
      Transaction transaction = messageMapper.getTransaction(messageReceived);
      if (transaction == Transaction.SAVE) {
        savePerson(messageMapper.parsePerson(messageReceived));
      } else if (transaction == Transaction.DELETE) {
        deletePerson(messageMapper.parsePersonId(messageReceived));
      } else {
        logger.warn("Unable to process messages with transaction type " + transaction + ". Message content: " + messageReceived);
      }
    } catch (JsonProcessingException e) {
      logger.warn("Failed to parse message: " + messageReceived);
    }
  }

  private void savePerson(Person person) throws SQLException {
    if (person.getId() == null || !personDAO.savePerson(person)) {
      logger.warn("Failed to save person with id = " + person.getId());
    } else {
      logger.info("Person " + person + " saved");
    }
  }

  private void deletePerson(Long personId) throws SQLException {
    if (personId == null || !personDAO.deletePerson(personId)) {
      logger.warn("Failed to remove person with id = " + personId);
    } else {
      logger.info("Person with id " + personId + " removed");
    }
  }
}
