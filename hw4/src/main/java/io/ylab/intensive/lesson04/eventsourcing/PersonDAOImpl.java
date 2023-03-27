package io.ylab.intensive.lesson04.eventsourcing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for interaction with DB table person. The {@link Person} object represents a record of the
 * table. It is assumed that db connection is provided.
 */
public class PersonDAOImpl implements PersonDAO {
  private final Connection connection;

  public PersonDAOImpl(Connection connection) {
    this.connection = connection;
  }

  @Override
  public List<Person> findAll() throws SQLException {
    String findAllQuery = "SELECT * FROM person";
    try (PreparedStatement preparedStatement = connection.prepareStatement(findAllQuery)) {
      ResultSet rs = preparedStatement.executeQuery();

      List<Person> allPeople = new ArrayList<>();
      while (rs.next()) {
        allPeople.add(resultSetLineToPerson(rs));
      }
      return allPeople;
    }
  }

  /**
   * Maps one line of {@link ResultSet} to a {@link Person} object
   *
   * @param rs {@link ResultSet} retrieved from query
   * @return {@link Person} object
   * @throws SQLException
   */
  private Person resultSetLineToPerson(ResultSet rs) throws SQLException {
    Person person = new Person();
    person.setId(rs.getLong(1));
    person.setName(rs.getString(2));
    person.setLastName(rs.getString(3));
    person.setMiddleName(rs.getString(4));
    return person;
  }

  @Override
  public Person findPerson(long personId) throws SQLException {
    String selectQuery = "SELECT * FROM person WHERE person_id = ?";
    try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
      ps.setLong(1, personId);
      ResultSet rs = ps.executeQuery();
      if (rs.isBeforeFirst()) {
        rs.next();
        return resultSetLineToPerson(rs);
      } else {
        return null;
      }
    }
  }

  /**
   * Saves {@link Person} object as a record in the database table. if no records with the same {@code person_id} found,
   * then a new recored is created. If a record with the same {@code person_id} is found, then all values for this record are
   * updated according the the {@link Person} object specified.
   *
   * @param person is the {@link Person} object to be saved
   * @return true if saving is successful
   * @throws SQLException
   */
  @Override
  public boolean savePerson(Person person) throws SQLException {
    if (findPerson(person.getId()) == null) {
      return addPerson(person) > 0;
    } else {
      return updatePerson(person) > 0;
    }
  }

  private int addPerson(Person newPerson) throws SQLException {
    String insertQuery = "INSERT INTO person (person_id, first_name, last_name, middle_name) VALUES (?, ?, ?, ?)";
    try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
      ps.setLong(1, newPerson.getId());
      ps.setString(2, newPerson.getName());
      ps.setString(3, newPerson.getLastName());
      ps.setString(4, newPerson.getMiddleName());
      return ps.executeUpdate();
    }
  }

  private int updatePerson(Person updatedPerson) throws SQLException {
    String updateQuery = "UPDATE person SET first_name=?, last_name=?, middle_name=? WHERE person_id=?";
    try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
      ps.setString(1, updatedPerson.getName());
      ps.setString(2, updatedPerson.getLastName());
      ps.setString(3, updatedPerson.getMiddleName());
      ps.setLong(4, updatedPerson.getId());
      return ps.executeUpdate();
    }
  }

  @Override
  public boolean deletePerson(long personId) throws SQLException {
    String deleteQuery = "DELETE FROM person WHERE person_id = ?";
    try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
      ps.setLong(1, personId);
      return ps.executeUpdate() > 0;
    }
  }
}
