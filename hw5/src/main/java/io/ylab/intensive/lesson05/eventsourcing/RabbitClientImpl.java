package io.ylab.intensive.lesson05.eventsourcing;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * This class is responsible for interacting with RabbitMQ. The construction of messages to be sent
 * is delegated to {@link MessageMapper}.<br/>
 * {@link RabbitClientImpl#QUEUE} is the queue name
 * {@link RabbitClientImpl#EXCHANGE} is the exhchange name
 * {@link RabbitClientImpl#channel} is the {@link Channel} object to RabbitMQ
 * The connection to RabbitMQ is established in the {@link RabbitClientImpl#init()} method.
 * The connection is closed in the {@link RabbitClientImpl#destroy()} method.
 *
 * @see MessageMapper
 */
@Component
public class RabbitClientImpl implements RabbitClient {
  private final ConnectionFactory connectionFactory;
  private Connection connection;
  private Channel channel;

  private final String QUEUE = "personQueue";
  private final String EXCHANGE = "exchange";
  private final Logger logger = LoggerFactory.getLogger(RabbitClientImpl.class);

  public RabbitClientImpl(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  @PostConstruct
  public void init() throws IOException, TimeoutException {
    try {
      connection = connectionFactory.newConnection();
      channel = connection.createChannel();
      channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.TOPIC);
      channel.queueDeclare(QUEUE, true, false, false, null);
      channel.queueBind(QUEUE, EXCHANGE, "person");
      logger.info("Connection to RabbitMQ is up.");
    } catch (Exception e) {
      e.printStackTrace();
      channel.close();
      connection.close();
    }
  }

  /**
   * Sends single message to RabbitMQ queue {@link RabbitClientImpl#QUEUE}
   *
   * @param message message to be sent
   * @throws IOException
   */
  @Override
  public void publish(String message) throws IOException {
    channel.basicPublish(EXCHANGE, "person", null, message.getBytes());
    logger.info("Message sent to queue " + QUEUE + ". Message content: " + message);
  }

  /**
   * Gets single message from RabbitMQ queue {@link RabbitClientImpl#QUEUE}
   *
   * @return message from queue
   * @throws IOException
   */
  @Override
  public String consume() throws IOException {
    GetResponse message = channel.basicGet(QUEUE, true);
    if (message != null) {
      return new String(message.getBody());
    }
    return null;
  }

  @PreDestroy
  public void destroy() throws IOException, TimeoutException {
    channel.close();
    connection.close();
    if (!channel.isOpen() && !connection.isOpen()) {
      logger.info("Connection to RabbitMQ is closed.");
    }
  }

}
