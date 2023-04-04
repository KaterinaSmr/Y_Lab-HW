package io.ylab.intensive.lesson05.messagefilter;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for masking curse words in incoming messages. Message is received from RabbitMQ queue.
 * Each word is checked to be a curse word. If a word is identified as a curse word - then all symbols except for
 * the first one and last one are replaced with '*'. If a word is not a curse word - then it remains as it is.
 * The result message is sent to RabbitMQ queue.</br>
 * A word is identified as a curse word if {@link DbClient#isBadWord(String)} returns true.
 * {@link RabbitClient} is used for communicating with RabbitMQ.
 *
 * @see RabbitClient
 * @see DbClient
 */
@Component
public class MessageProcessorImpl implements MessageProcessor {
  private final RabbitClient rabbitClient;
  private final DbClient dbClient;
  private final String wordBorder = "([\\s.,;:!?]|^|$)";

  public MessageProcessorImpl(RabbitClient rabbitClient, DbClient dbClient) {
    this.rabbitClient = rabbitClient;
    this.dbClient = dbClient;
  }

  /**
   * Gets single message from queue, performs curse words masking and sends updated message to the queue.
   *
   * @throws IOException
   * @throws SQLException
   */
  public void process() throws IOException, SQLException {
    String message = rabbitClient.consume();
    if (message == null || message.isEmpty()) {
      return;
    }

    String[] words = message.split(wordBorder);
    StringBuilder result = new StringBuilder(message);
    for (String s : words) {
      if (dbClient.isBadWord(s)) {
        maskBadWord(result, s);
      }
    }

    rabbitClient.publish(result.toString());
  }

  /**
   * Replaces specific curse word in a message to a masked version.
   * First, the curse word is found withing the message itself, taking into account valid word limiting characters,
   * which is one of {@link MessageProcessorImpl#wordBorder} characters.
   * Next, withing this match the curse word itself is searched. Based on these 2 searches, the start position of the
   * word to be replaced is determined. First and last symbols of the bad word are remaining the same, every other symbol
   * is replaces with '*'.
   *
   * @param sb      message, in which the curse word should be masked
   * @param badWord word to be masked
   */
  private void maskBadWord(StringBuilder sb, String badWord) {
    if (badWord.length() < 3) {
      return;
    }
    Matcher matcher = Pattern.compile(wordBorder + badWord + wordBorder).matcher(sb);

    if (matcher.find()) {
      String matchWithBorders = matcher.group(0);
      Matcher wordMatcher = Pattern.compile(badWord).matcher(matchWithBorders);

      if (wordMatcher.find()) {
        int maskingStartPosition = matcher.start() + wordMatcher.start() + 1;
        int maskingLength = badWord.length() - 2;
        sb.replace(maskingStartPosition, maskingStartPosition + maskingLength, "*".repeat(maskingLength));
      }
    }
  }

}
