package io.ylab.intensive.lesson05.messagefilter;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * This class is responsible for interacting with RabbitMQ.
 * {@link RabbitClientImpl#INPUT} is the queue name for input messages.
 * {@link RabbitClientImpl#EXCHANGE} is the exhchange name.
 * {@link RabbitClientImpl#OUTPUT} is the queue name for input messages.
 * The connection to RabbitMQ is established in the {@link RabbitClientImpl#init()} method.
 * The connection is closed in the {@link RabbitClientImpl#destroy()} method.
 */
@Component
public class RabbitClientImpl implements RabbitClient {
  private final ConnectionFactory connectionFactory;
  private Connection connection;
  private Channel channel;

  private final String INPUT = "input";
  private final String OUTPUT = "output";
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
      channel.queueDeclare(INPUT, true, false, false, null);
      channel.queueBind(INPUT, EXCHANGE, "in");
      channel.queueDeclare(OUTPUT, true, false, false, null);
      channel.queueBind(OUTPUT, EXCHANGE, "out");
      logger.info("Connection to RabbitMQ is up.");
    } catch (Exception e) {
      destroy();
      throw e;
    }
  }

  /**
   * Sends single message to RabbitMQ queue {@link RabbitClientImpl#OUTPUT}
   *
   * @param message message to be sent
   * @throws IOException
   */
  @Override
  public void publish(String message) throws IOException {
    channel.basicPublish(EXCHANGE, "out", null, message.getBytes());
    logger.info("Message sent to queue " + OUTPUT + ". Message content: " + message);
  }

  /**
   * Gets single message from RabbitMQ queue {@link RabbitClientImpl#INPUT}
   *
   * @return message from queue
   * @throws IOException
   */
  @Override
  public String consume() throws IOException {
    GetResponse message = channel.basicGet(INPUT, true);
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
