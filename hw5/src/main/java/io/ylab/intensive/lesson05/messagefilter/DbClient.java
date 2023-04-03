package io.ylab.intensive.lesson05.messagefilter;

import java.sql.SQLException;

public interface DbClient {
  boolean isBadWord(String word) throws SQLException;
}
