package io.ylab.intensive.lesson04.eventsourcing.api;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import io.ylab.intensive.lesson04.eventsourcing.MessageMapper;
import io.ylab.intensive.lesson04.eventsourcing.MessageMapperImpl;
import io.ylab.intensive.lesson04.eventsourcing.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This class is responsible for publishing messages to RabbitMQ. The consturction of messages to be sent
 * is delegated to {@link MessageMapper}.<br/>
 * {@link PersonPublisherImpl#QUEUE} is the queue name
 * {@link PersonPublisherImpl#EXCHANGE} is the exhchange name
 * {@link PersonPublisherImpl#channel} is the {@link Channel} object to RabbitMQ
 * @see MessageMapper
 */
public class PersonPublisherImpl implements PersonPublisher{
  private final Channel channel;
  private final String QUEUE = "personQueue";
  private final String EXCHANGE = "personExch";
  private final MessageMapper messageMapper;
  private final Logger logger = LoggerFactory.getLogger(PersonApiImpl.class);

  public PersonPublisherImpl(Channel channel) throws IOException {
    this.channel = channel;
    this.messageMapper = new MessageMapperImpl();
    channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.TOPIC);
    channel.queueDeclare(QUEUE, true, false, false, null);
    channel.queueBind(QUEUE, EXCHANGE, "*");
  }

  /**
   * Publishes message to save {@link Person} object to the queue. First the {@link Person} object is wrapped into a
   * message in JSON format with the {@link MessageMapper}. And then this message is published with the
   * {@link PersonPublisherImpl#publish} method
   * @param person is {@link Person} object to be saved
   * @throws IOException
   */
  @Override
  public void savePerson(Person person) throws IOException {
    publish(messageMapper.wrapPerson(person));
  }

  private void publish(String message) throws IOException {
    channel.basicPublish(EXCHANGE, "*", null, message.getBytes());
    logger.info("Message sent to queue " + QUEUE + ". Message content: " + message);
  }

  /**
   * Publishes message to remove particular {@link Person} object to the queue.  First the {@link Person} object
   * is wrapped into a message in JSON format with the {@link MessageMapper}. And then this message is published with the
   * {@link PersonPublisherImpl#publish} method
   * @param personId is {@link Long} value of identificaion of a {@link Person} object to be removed
   * @throws IOException
   */
  @Override
  public void removePerson(Long personId) throws IOException {
    publish(messageMapper.wrapPersonId(personId));
  }
}
