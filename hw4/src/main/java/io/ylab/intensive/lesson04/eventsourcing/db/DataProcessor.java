package io.ylab.intensive.lesson04.eventsourcing.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeoutException;

public interface DataProcessor {
  void processMessages() throws IOException, TimeoutException, SQLException;

}
