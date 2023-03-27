package io.ylab.intensive.lesson04.eventsourcing;

import java.sql.SQLException;
import java.util.List;

public interface PersonDAO {

  List<Person> findAll() throws SQLException;

  Person findPerson(long personId) throws SQLException;

  boolean savePerson(Person person) throws SQLException;


  boolean deletePerson(long personId) throws SQLException;
}
