package io.ylab.intensive.lesson05.messagefilter;

import java.io.IOException;

public interface RabbitClient {
  void publish(String message) throws IOException;

  String consume() throws IOException;
}
