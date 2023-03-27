package io.ylab.intensive.lesson04.filesort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Sorts the source file with longs in descending order and writes the result to a new file<br/>
 * {@value FileSortImpl#BATCH_SIZE} is a batch size for {@link PreparedStatement} batch update. The measurements show
 * the best performance with this value. Further increase of batch size does not improve productivity.<br/>
 * {@value FileSortImpl#BUFFER_SIZE} is buffer size for {@link BufferedReader} reading files.
 */
public class FileSortImpl implements FileSorter {
  private DataSource dataSource;
  private static final int BATCH_SIZE = 500;
  private static final int BUFFER_SIZE = 32768;

  private final Logger logger = LoggerFactory.getLogger(FileSortImpl.class);

  public FileSortImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public File sort(File data) throws SQLException, IOException {
    try (Connection connection = dataSource.getConnection()) {
      readFromFileToDBWithBatch(data, connection);

//      у меня без батча заняло около 3х минут
//      readFromFileToDBNoBatch(data,connection);

      File sortedFile = new File("sortedData.txt");
      return writeSortedFromDBToFile(sortedFile, connection);
    }
  }

  /**
   * Reads long values from the source file and writes them to the database table {@code numbers}.
   * Reading from file is done with {@link BufferedReader} and {@value FileSortImpl#BUFFER_SIZE} buffer size,
   * Writing to db is done with {@link PreparedStatement} batch update and {@value FileSortImpl#BATCH_SIZE} size of
   * each batch.
   *
   * @param sourceFile the source file with long values
   * @param connection the connection to db with {@code numbers} table
   * @throws IOException  If an I/O error occurs
   * @throws SQLException if a database access error occurs or this method is called on a closed connection
   */
  private void readFromFileToDBWithBatch(File sourceFile, Connection connection) throws IOException, SQLException {
    logger.info("Batch insertion to db start at " + LocalDateTime.now());
    long start = System.currentTimeMillis();
    String query = "INSERT INTO numbers VALUES (?)";
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(sourceFile), BUFFER_SIZE);
         PreparedStatement ps = connection.prepareStatement(query)) {
      String nextLine;
      int batchItems = 0;
      while ((nextLine = bufferedReader.readLine()) != null) {
        ps.setLong(1, Long.parseLong(nextLine));
        ps.addBatch();
        if (++batchItems % BATCH_SIZE == 0) {
          ps.executeBatch();
        }
      }
      ps.executeBatch();
    }
    logger.info("Batch insertion to db end at " + LocalDateTime.now() + " Time elapsed: " + (System.currentTimeMillis() - start) + " ms");
  }

  private void readFromFileToDBNoBatch(File sourceFile, Connection connection) throws IOException, SQLException {
    logger.info("No-batch insertion to db start at " + LocalDateTime.now());
    long start = System.currentTimeMillis();
    String query = "INSERT INTO numbers VALUES (?)";
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(sourceFile), BUFFER_SIZE);
         PreparedStatement ps = connection.prepareStatement(query)) {
      String nextLine;
      while ((nextLine = bufferedReader.readLine()) != null) {
        ps.setLong(1, Long.parseLong(nextLine));
        ps.executeUpdate();
      }
    }
    logger.info("No-batch insertion to db end at " + LocalDateTime.now() + " Time elapsed: " + (System.currentTimeMillis() - start) + " ms");
  }

  /**
   * Reads long values from db table {@code numbers} and writes them to the destination file.
   * The values selected from db are <b>ordered descending</b> and written in the same order to the output file.
   *
   * @param destinationFile the sorted output file
   * @param connection      the connection to db with the {@code numbers} table to select data from
   * @return {@link File} sorted file
   * @throws FileNotFoundException If the given file object does not denote an existing, writable regular file and a new regular file of that name cannot be created, or if some other error occurs while opening or creating the file
   * @throws SQLException          if a database access error occurs or this method is called on a closed connection
   */
  private File writeSortedFromDBToFile(File destinationFile, Connection connection) throws FileNotFoundException, SQLException {
    String query = "SELECT * FROM numbers ORDER BY numbers DESC";
    try (PrintWriter printWriter = new PrintWriter(destinationFile); PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        printWriter.println(rs.getLong(1));
      }
      printWriter.flush();
    }
    return destinationFile;
  }

}
