package io.ylab.intensive.lesson04.eventsourcing.api;

import io.ylab.intensive.lesson04.eventsourcing.Person;

import java.io.IOException;

public interface PersonPublisher {

  void savePerson(Person person) throws IOException;

  void removePerson(Long personId) throws IOException;
}
