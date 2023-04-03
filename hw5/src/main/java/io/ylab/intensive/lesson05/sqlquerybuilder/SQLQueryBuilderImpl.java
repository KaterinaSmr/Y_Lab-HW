package io.ylab.intensive.lesson05.sqlquerybuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SQLQueryBuilderImpl implements SQLQueryBuilder {
  private final DataSource dataSource;
  private final Connection connection;
  private final Logger logger = LoggerFactory.getLogger(SQLQueryBuilderImpl.class);

  public SQLQueryBuilderImpl(DataSource dataSource) throws SQLException {
    this.dataSource = dataSource;
    this.connection = dataSource.getConnection();
    logger.info("Connection to db is up");
  }

  @Override
  public String queryForTable(String tableName) throws SQLException {
    DatabaseMetaData metaData = connection.getMetaData();
    StringBuilder sb = new StringBuilder("SELECT ");

    try (ResultSet columnsResultSet = metaData.getColumns("postgres", null, tableName, null)) {
      if (tableName == null || !columnsResultSet.isBeforeFirst()) {
        logger.warn("No columns for table " + tableName + " were found");
        return null;
      }
      while (columnsResultSet.next()) {
        sb.append(columnsResultSet.getString(4));
        sb.append(", ");
      }
    }
    sb.delete(sb.length() - 2, sb.length() - 1);
    sb.append("FROM ");
    sb.append(tableName);
    sb.append(";");
    return sb.toString();
  }

  @Override
  public List<String> getTables() throws SQLException {
    DatabaseMetaData metaData = connection.getMetaData();
    List<String> result = new ArrayList<>();
    //из поиска исключаем sequence, view, индексы и т.д., включаем только таблицы
    try (ResultSet rs1 = metaData.getTables(null, null, null,
            new String[]{"FOREIGN TABLE", "SYSTEM TABLE", "SYSTEM TOAST TABLE", "TABLE", "TEMPORARY TABLE"})) {
      while (rs1.next()) {
        result.add(rs1.getString(3));
      }
    }
    return result;
  }

  @PreDestroy
  public void destroy() throws SQLException {
    connection.close();
    logger.info("Connection to db is closed");
  }
}
