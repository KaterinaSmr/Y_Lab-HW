package io.ylab.intensive.lesson04.movie;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

public class MovieLoaderImpl implements MovieLoader {
  private DataSource dataSource;
  private  Function<String, Movie> converterStringToMovie;

  public MovieLoaderImpl(DataSource dataSource) {
    this.dataSource = dataSource;
    this.converterStringToMovie = this::convert;
  }

  @Override
  public void loadData(File file) throws IOException, SQLException {
    Collection<Movie> allMovies = readMoviesFromFile(file);
    loadMoviesToDB(allMovies);
  }

  /**
   * Reads the file specified line by line and converts each line to the {@link Movie} object.
   * {@link MovieLoaderImpl#convert(String)} method is used for string to {@link Movie}
   * conversion
   *
   * @param file the source file
   * @return List of movies
   * @throws IOException
   * @throws NumberFormatException          if the source file contains incorrect symbols in numeric fields
   * @throws ArrayIndexOutOfBoundsException if the source file contains incorrect number of columns for at least one row
   */
  private Collection<Movie> readMoviesFromFile(File file) throws IOException {
    Collection<Movie> allMovies = new ArrayList<>();
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, Charset.forName("cp1251")))) {
      bufferedReader.readLine();
      bufferedReader.readLine();
      String nextLine;
      while ((nextLine = bufferedReader.readLine()) != null) {
        allMovies.add(converterStringToMovie.apply(nextLine));
      }
    }
    return allMovies;
  }

  /**
   * Converts string to {@link Movie} object. Empty values for Integer fields will be converted to null
   *
   * @param nextLine string to be converted to {@link Movie}
   * @return {@link Movie} object
   * @throws NumberFormatException          if the source file contains incorrect symbols in numeric fields
   * @throws ArrayIndexOutOfBoundsException if the source file contains incorrect number of columns for at least one row
   */
  private Movie convert(String nextLine) {
    Movie movie = new Movie();
    String[] movieData = nextLine.split(";");
    movie.setYear(parseStringToIntOrNull(movieData[0]));
    movie.setLength(parseStringToIntOrNull(movieData[1]));
    movie.setTitle(movieData[2]);
    movie.setSubject(movieData[3]);
    movie.setActors(movieData[4]);
    movie.setActress(movieData[5]);
    movie.setDirector(movieData[6]);
    movie.setPopularity(parseStringToIntOrNull(movieData[7]));
    movie.setAwards(movieData[8].equalsIgnoreCase("Yes"));
    return movie;
  }

  public Integer parseStringToIntOrNull(String s) {
    return s == null || s.isEmpty() ? null : Integer.parseInt(s);
  }

  /**
   * Uploads collection of {@link Movie} objects to database with batch processing. Batch size is not specified,
   * so all the values inserted in one batch.
   * Null numeric fields in {@link Movie} object are converted to {@code <null>} value in db table
   *
   * @param allMovies
   * @throws SQLException if a database access error occurs or this method is called on a closed connection
   */
  private void loadMoviesToDB(Collection<Movie> allMovies) throws SQLException {
    String query = "INSERT INTO movie (year, length, title, subject, actors, actress, director, popularity, awards)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement ps = connection.prepareStatement(query)) {

      for (Movie movie : allMovies) {
        setIntOrNull(ps, 1, movie.getYear());
        setIntOrNull(ps, 2, movie.getLength());
        ps.setString(3, movie.getTitle());
        ps.setString(4, movie.getSubject());
        ps.setString(5, movie.getActors());
        ps.setString(6, movie.getActress());
        ps.setString(7, movie.getDirector());
        setIntOrNull(ps, 8, movie.getPopularity());
        ps.setBoolean(9, movie.getAwards());
        ps.addBatch();
      }
      ps.executeBatch();
    }
  }

  private void setIntOrNull(PreparedStatement ps, int index, Integer data) throws SQLException {
    if (data == null) {
      ps.setNull(index, Types.INTEGER);
    } else {
      ps.setInt(index, data);
    }
  }

}
