package io.ylab.intensive.lesson04.eventsourcing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for wrapping Objects into JSON messages and back.
 * 2 types of messages are supported: {@code SAVE} - message requesting saving of a {@link Person} object;
 * {@code DELETE} - message requesting deletion of a {@link Person} object.<br/>
 * All messages have following structure: { {@value MessageMapperImpl#TRANSACTION_KEY} : "", {@value MessageMapperImpl#VALUE_KEY} : ""}.
 * To differentiate message types, the method {@link MessageMapper#getTransaction} should be used.
 * Values for {@value MessageMapperImpl#TRANSACTION_KEY} are instances of enum {@link Transaction} corresponding
 * to each message type.<br/>
 * The {@value MessageMapperImpl#VALUE_KEY} contains either {@link Person} object for message type  {@code SAVE},
 * or a {@link Long} value for message type {@code DELETE}. Message type ({@link Transaction} type) should be
 * determined before attempting to parse the {@value MessageMapperImpl#VALUE_KEY} section, otherwise
 * {@link MismatchedInputException} is thrown.
 */
public class MessageMapperImpl implements MessageMapper {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final String TRANSACTION_KEY = "transaction";
  private static final String VALUE_KEY = "value";

  /**
   * Wraps the {@link Person} into JSON message with type {@code SAVE}.
   *
   * @param person the {@link Person} object to be wrapped
   * @return String representation of JSON wrapping the {@link Person} object
   * @throws JsonProcessingException
   */
  @Override
  public String wrapPerson(Person person) throws JsonProcessingException {
    Map<String, Object> jsonToSend = new HashMap<>();
    jsonToSend.put(TRANSACTION_KEY, Transaction.SAVE);
    jsonToSend.put(VALUE_KEY, person);
    return objectMapper.writeValueAsString(jsonToSend);
  }

  /**
   * Wraps the {@link Long} value of {@code personId} into JSON message with type {@code DELETE}.
   *
   * @param personId the id of the {@link Person} object to be deleted
   * @return String representation of JSON wrapping the {@code personId}
   * @throws JsonProcessingException
   */
  @Override
  public String wrapPersonId(Long personId) throws JsonProcessingException {
    Map<String, Object> jsonToSend = new HashMap<>();
    jsonToSend.put(TRANSACTION_KEY, Transaction.DELETE);
    jsonToSend.put(VALUE_KEY, personId);
    return objectMapper.writeValueAsString(jsonToSend);
  }

  /**
   * Retrieves message type from the String representation of the message received. Message type should be an instance of
   * {@link Transaction} otherwise {@link com.fasterxml.jackson.databind.exc.InvalidFormatException} is thrown.
   *
   * @param message incoming message to be parsed
   * @return {@link Transaction} object or {@code null} in case of missing mapping
   * @throws JsonProcessingException in case of wrong value for the {@value MessageMapperImpl#TRANSACTION_KEY}
   */
  @Override
  public Transaction getTransaction(String message) throws JsonProcessingException {
    JsonNode jsonObj = objectMapper.readTree(message);
    return objectMapper.treeToValue(jsonObj.get(TRANSACTION_KEY), Transaction.class);
  }

  /**
   * Parses {@link Person} object from the String representation of the message received. This method should only be called
   * for message type {@code SAVE} otherwise {@link MismatchedInputException} is thrown
   *
   * @param message incoming message to be parsed
   * @return parsed {@link Person} object
   * @throws JsonProcessingException in case of message content inconsistent with {@link Person} under the
   *                                 {@value MessageMapperImpl#VALUE_KEY} key
   */
  @Override
  public Person parsePerson(String message) throws JsonProcessingException {
    JsonNode jsonObj = objectMapper.readTree(message);
    return objectMapper.treeToValue(jsonObj.get(VALUE_KEY), Person.class);
  }

  /**
   * Parses the String representation of the message received into {@link Long} value for the {@code personId}.
   * This method should only be called for message type {@code DELETE} otherwise {@link MismatchedInputException} is thrown
   *
   * @param message incoming message to be parsed
   * @return {@link Long} value of {@code personId}
   * @throws JsonProcessingException in case of message content inconsistent with {@link Long} under the
   *                                 {@value MessageMapperImpl#VALUE_KEY} key
   */
  @Override
  public Long parsePersonId(String message) throws JsonProcessingException {
    JsonNode jsonObj = objectMapper.readTree(message);
    return objectMapper.treeToValue(jsonObj.get(VALUE_KEY), Long.class);
  }
}
