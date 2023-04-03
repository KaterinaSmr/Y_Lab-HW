package io.ylab.intensive.lesson05.eventsourcing.db;

import java.io.IOException;
import java.sql.SQLException;

public interface MessageScheduler {
  void start() throws SQLException, IOException;
}
