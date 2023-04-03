package io.ylab.intensive.lesson05.messagefilter;

import io.ylab.intensive.lesson05.eventsourcing.PersonDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.List;

/**
 * This class is responsible for interaction with DB. The connection to db is established in the {@link DbClientImpl#init()}
 * method. The connection is closed in the {@link PersonDAOImpl#destroy()} method
 */
@Component
public class DbClientImpl implements DbClient {
  private final DataSource dataSource;
  private final Connection connection;
  private final Logger logger = LoggerFactory.getLogger(DbClientImpl.class);
  private final String TABLE_NAME = "filter";
  private final WordsToFilter wordsToFilter;

  public DbClientImpl(DataSource dataSource, WordsToFilter wordsToFilter) throws SQLException {
    this.dataSource = dataSource;
    this.connection = dataSource.getConnection();
    this.wordsToFilter = wordsToFilter;
    logger.info("Connection to db is up");
  }

  @PostConstruct
  public void init() throws SQLException, IOException {
    try {
      clearTableIfExistsOrCreate();
      fillTable();
    } catch (Exception e) {
      destroy();
      throw e;
    }
  }

  private boolean tableExists(String tableName) throws SQLException {
    DatabaseMetaData metaData = connection.getMetaData();
    try (ResultSet resultSet = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
      return resultSet.next();
    }
  }

  /**
   * Clears table {@link DbClientImpl#TABLE_NAME} if exists. Creates table {@link DbClientImpl#TABLE_NAME}
   * if no table with the same name exists.
   * @throws SQLException
   */
  private void clearTableIfExistsOrCreate() throws SQLException {
    String query;
    if (tableExists(TABLE_NAME)) {
      query = "delete from " + TABLE_NAME;
    } else {
      query = "create table if not exists " + TABLE_NAME + " (data varchar)";
    }
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.executeUpdate();
    }
  }

  /**
   * Fills the table {@link DbClientImpl#TABLE_NAME} with the values provided by {@link WordsToFilter}
   * @throws IOException
   * @throws SQLException
   * @see WordsToFilter
   */
  private void fillTable() throws IOException, SQLException {
    String query = "INSERT INTO " + TABLE_NAME + " VALUES (?)";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      connection.setAutoCommit(false);
      List<String> wordsToFilterList = wordsToFilter.getWordsToFilter();

      for (String s : wordsToFilterList) {
        ps.setString(1, s);
        ps.addBatch();
      }
      ps.executeBatch();
      connection.commit();
    }
  }

  /**
   * Checks whether the string specified is present in db table ignore case
   * @param word the string to be searched in db
   * @return true if the string specified exist in db ignore case
   * @throws SQLException
   */
  @Override
  public boolean isBadWord(String word) throws SQLException {
    String query = "SELECT * FROM " + TABLE_NAME + " WHERE data ILIKE ?";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setString(1, word);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    }
  }

  @PreDestroy
  public void destroy() throws SQLException {
    connection.close();
    if (connection.isClosed()) {
      logger.info("Connection to db is closed");
    }
  }
}
