package io.ylab.intensive.lesson05.messagefilter;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Repetitively calls the {@link MessageProcessor#process()}
 * @see MessageProcessor
 */
@Component
public class MessageSchedulerImpl implements MessageScheduler {
  private final MessageProcessor messageProcessor;

  public MessageSchedulerImpl(MessageProcessor messageProcessor) {
    this.messageProcessor = messageProcessor;
  }

  public void start() throws SQLException, IOException, InterruptedException {
    while (!Thread.currentThread().isInterrupted()) {
      Thread.sleep(500);
      messageProcessor.process();
    }
  }
}
