package io.ylab.intensive.lesson05.messagefilter;

import java.io.IOException;
import java.sql.SQLException;

public interface MessageProcessor {
  void process() throws IOException, SQLException;
}
