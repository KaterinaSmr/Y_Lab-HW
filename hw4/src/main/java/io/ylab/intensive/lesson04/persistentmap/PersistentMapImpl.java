package io.ylab.intensive.lesson04.persistentmap;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores key-value map in a database. Multiple maps allowed. Maps differentiated by names.<br/>
 * {@link PersistentMapImpl#mapName} is map name. Map name is not allowed to be {@code null}.
 * Key and value {@code null} values are allowed. <br/>
 * This implementation <b>does not support multi-thread access </b>.
 */
public class PersistentMapImpl implements PersistentMap {

  private DataSource dataSource;
  private String mapName;

  public PersistentMapImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * Initiates a new map, or checkout existing map.
   *
   * @param name the name of the map
   * @throws PersistenceMapMissingNameException if {@link PersistentMapImpl#mapName} is {@code null}
   */
  @Override
  public void init(String name) {
    this.mapName = name;
    throwExceptionIfMapNameIsNull();
  }

  /**
   * Returns true if this map contains a mapping for the specified key.
   * It uses {@code SELECT COUNT} request to database table to check if lines with this key exist.
   *
   * @param key key whose presence in this map is to be tested
   * @return true if this map contains a mapping for the specified key
   * @throws PersistenceMapMissingNameException if {@link PersistentMapImpl#mapName} is {@code null}
   * @throws SQLException                       if a database access error occurs or this method is called on a closed connection
   */
  @Override
  public boolean containsKey(String key) throws SQLException {
    throwExceptionIfMapNameIsNull();
    // мы не можем реюзать get() потому что он возвращает одинаковые значения null в случае если ключа нет
    // и если ключ есть и значение для него явно указано как null
    String query = key == null ? "SELECT COUNT(*) FROM persistent_map WHERE map_name = ? AND KEY IS NULL"
            : "SELECT COUNT(*) FROM persistent_map WHERE map_name = ? AND KEY = ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setString(1, mapName);
      if (key != null) {
        ps.setString(2, key);
      }

      //я не забыла про ResultSet - он автоматически закрывается, когда закрывается сгенерировавший его Statement
      ResultSet rs = ps.executeQuery();
      rs.next();
      return rs.getInt(1) > 0;
    }
  }

  /**
   * Returns a {@link List} of the keys contained in this map. {@code null} keys are included if present.
   *
   * @return a {@link List} of the keys contained in this map
   * @throws PersistenceMapMissingNameException if {@link PersistentMapImpl#mapName} is {@code null}
   * @throws SQLException                       if a database access error occurs or this method is called on a closed connection
   */
  @Override
  public List<String> getKeys() throws SQLException {
    throwExceptionIfMapNameIsNull();
    String query = "SELECT key FROM persistent_map WHERE map_name = ?";
    List<String> allKeys = new ArrayList<>();

    try (Connection connection = dataSource.getConnection();
         PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setString(1, mapName);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        allKeys.add(rs.getString(1));
      }
    }
    return allKeys;
  }

  /**
   * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key. <br/>
   * This map permits {@code null} values, so a return value of {@code null} does not necessarily indicate
   * that the map contains no mapping for the key; it's also possible that the map explicitly maps the key to {@code null}.
   * The {@link PersistentMap#containsKey} operation may be used to distinguish these two cases.
   *
   * @param key the key whose associated value is to be returned
   * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
   * @throws PersistenceMapMissingNameException if {@link PersistentMapImpl#mapName} is {@code null}
   * @throws SQLException                       if a database access error occurs or this method is called on a closed connection
   */
  @Override
  public String get(String key) throws SQLException {
    throwExceptionIfMapNameIsNull();
    String query = key == null ? "SELECT value FROM persistent_map WHERE map_name = ? AND KEY IS NULL"
            : "SELECT value FROM persistent_map WHERE map_name = ? AND KEY = ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setString(1, mapName);
      if (key != null) {
        ps.setString(2, key);
      }
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        return rs.getString(1);
      }
      return null;
    }
  }

  /**
   * Removes the mapping for a key from this map if it is present. Does nothing if the key is not present in the map.
   * The return type of this method is {@code void}, so there is no way to find out whether mapping was removed
   * or it was not present. To differentiate these two cases, method {@link PersistentMapImpl#containsKey} should
   * be used before and after removal.
   *
   * @param key the key whose mapping is to be removed from the map
   * @throws PersistenceMapMissingNameException if {@link PersistentMapImpl#mapName} is {@code null}
   * @throws SQLException                       if a database access error occurs or this method is called on a closed connection
   */
  @Override
  public void remove(String key) throws SQLException {
    throwExceptionIfMapNameIsNull();
    try (Connection connection = dataSource.getConnection()) {
      remove(key, connection);
    }
  }

  private void remove(String key, Connection connection) throws SQLException {
    String query = key == null ? "DELETE FROM persistent_map WHERE map_name = ? AND KEY IS NULL"
            : "DELETE FROM persistent_map WHERE map_name = ? AND KEY = ?";
    try (PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setString(1, mapName);
      if (key != null) {
        ps.setString(2, key);
      }
      ps.executeUpdate();
    }
  }

  /**
   * Associates the specified value with the specified key in this map. If the map previously contained a mapping
   * for the key, first, the old value is removed from table with the {@link PersistentMapImpl#remove(String, Connection)} method;
   * and then the new mapping is added. <br/>
   * These two operations are done in one transaction. If the process is disrupted, uncommitted changes are rolled back.
   *
   * @param key   key with which the specified value is to be associated
   * @param value value to be associated with the specified key
   * @throws PersistenceMapMissingNameException if {@link PersistentMapImpl#mapName} is {@code null}
   * @throws SQLException                       if a database access error occurs or this method is called on a closed connection
   */
  @Override
  public void put(String key, String value) throws SQLException {
    throwExceptionIfMapNameIsNull();
    Connection connection = dataSource.getConnection();
    try {
      connection.setAutoCommit(false);

      remove(key, connection);
      put(key, value, connection);

      connection.commit();
    } catch (Exception e) {
      if (connection != null) {
        connection.rollback();
      }
    } finally {
      connection.close();
    }
  }

  private void put(String key, String value, Connection connection) throws SQLException {
    String insertQuery = "INSERT INTO persistent_map (map_name, KEY, value) VALUES (?, ?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
      ps.setString(1, mapName);
      ps.setString(2, key);
      ps.setString(3, value);
      ps.executeUpdate();
    }
  }

  /**
   * Removes all mappings for the current map from db.
   *
   * @throws PersistenceMapMissingNameException if {@link PersistentMapImpl#mapName} is {@code null}
   * @throws SQLException                       if a database access error occurs or this method is called on a closed connection
   */
  @Override
  public void clear() throws SQLException {
    throwExceptionIfMapNameIsNull();
    String query = "DELETE FROM persistent_map WHERE map_name = ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement ps = connection.prepareStatement(query)) {
      ps.setString(1, mapName);
      ps.executeUpdate();
    }
  }

  private void throwExceptionIfMapNameIsNull() {
    if (mapName == null) {
      throw new PersistenceMapMissingNameException();
    }
  }
}
