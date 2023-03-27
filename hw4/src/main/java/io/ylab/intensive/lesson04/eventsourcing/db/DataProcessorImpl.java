package io.ylab.intensive.lesson04.eventsourcing.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import io.ylab.intensive.lesson04.eventsourcing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

/**
 * This class is responsible for receiving and processing messages from RabbitMQ.
 * The outcome of a message processing should be an update to the db. The message parsing is delegated to a
 * {@link MessageMapper} object. The update to the db is delegated to a {@link PersonDAO} object.
 * {@link DataProcessorImpl#QUEUE} is the name of the queue from which the messages are retrieved.
 */
public class DataProcessorImpl implements DataProcessor {
  private final ConnectionFactory connectionFactory;
  private final String QUEUE = "personQueue";
  private final MessageMapper messageMapper;

  private final DataSource dataSource;
  private PersonDAO personDAO;

  private final Logger logger = LoggerFactory.getLogger(DataProcessorImpl.class);

  public DataProcessorImpl(DataSource dataSource, ConnectionFactory connectionFactory) {
    this.dataSource = dataSource;
    this.connectionFactory = connectionFactory;
    this.messageMapper = new MessageMapperImpl();
  }

  /**
   * Creates connection to RabbitMQ and database. Waits for messages from the queue {@link DataProcessorImpl#QUEUE}
   * in RabbitMQ and delegates message processing to {@link DataProcessorImpl#process} method
   *
   * @throws IOException
   * @throws TimeoutException
   * @throws SQLException
   */
  @Override
  public void processMessages() throws IOException, TimeoutException, SQLException {
    //предполагаем что где то у нас будет код, устанавливающий Thread.currentThread().interrupt();
    //тогда ресурсы будут закрыты
    try (Connection connection = connectionFactory.newConnection();
         Channel channel = connection.createChannel();
         java.sql.Connection dbConnection = dataSource.getConnection()) {
      personDAO = new PersonDAOImpl(dbConnection);
      logger.info("Data Processor is up. Waiting for messages");
      while (!Thread.currentThread().isInterrupted()) {
        GetResponse message = channel.basicGet(QUEUE, true);
        if (message != null) {
          String received = new String(message.getBody());
          process(received);
        }
      }
    }
  }

  /**
   * Parses the message type with the {@link MessageMapper} and posts updates to the database with the {@link PersonDAO}
   * depending on the message type.
   *
   * @param messageReceived the message to be processed
   * @throws SQLException
   * @see MessageMapper
   */
  public void process(String messageReceived) throws SQLException {
    try {
      Transaction transaction = messageMapper.getTransaction(messageReceived);
      switch (transaction) {
        case SAVE:
          savePerson(messageMapper.parsePerson(messageReceived));
          break;
        case DELETE:
          deletePerson(messageMapper.parsePersonId(messageReceived));
          break;
        default:
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
