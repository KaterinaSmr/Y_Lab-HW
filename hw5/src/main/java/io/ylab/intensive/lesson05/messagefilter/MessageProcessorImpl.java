package io.ylab.intensive.lesson05.messagefilter;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;

/**
 * This class is responsible for masking curse words in incoming messages. Message is received from RabbitMQ queue.
 * Each word is checked to be a curse word. If a word is identified as a curse word - then all symbols except for
 * the first one and last one are replaced with '*'. If a word is not a curse word - then it remains as it is.
 * The result message is sent to RabbitMQ queue.</br>
 * A word is identified as a curse word if {@link DbClient#isBadWord(String)} returns true.
 * {@link RabbitClient} is used for communicating with RabbitMQ.
 * @see RabbitClient
 * @see DbClient
 */
@Component
public class MessageProcessorImpl implements MessageProcessor {
  private final RabbitClient rabbitClient;
  private final DbClient dbClient;

  public MessageProcessorImpl(RabbitClient rabbitClient, DbClient dbClient) {
    this.rabbitClient = rabbitClient;
    this.dbClient = dbClient;
  }

  /**
   * Gets single message from queue, performs curse words masking and sends updated message to the queue.
   * @throws IOException
   * @throws SQLException
   */
  public void process() throws IOException, SQLException {
    String message = rabbitClient.consume();
    if (message == null || message.isEmpty()) {
      return;
    }

    String[] words = message.split("[\\s\\n\\r .,;:!?]");
    for (String s : words) {
      if (dbClient.isBadWord(s)) {
        message = message.replaceFirst(s, replacementWord(s));
      }
    }

    rabbitClient.publish(message);
  }

  /**
   * Masks the string specified, so that only first and last symbols are the same and all other symbols are replaced
   * with '*'. The length of the string remains the same.
   * @param word curse word to be masked
   * @return masked version of the string specified
   */
  private String replacementWord(String word) {
    if (word.length() <= 2) {
      return word;
    }
    StringBuilder replacement = new StringBuilder();
    replacement.append(word.charAt(0));
    replacement.append("*".repeat(word.length() - 2));
    replacement.append(word.charAt(word.length() - 1));
    return replacement.toString();
  }

}
