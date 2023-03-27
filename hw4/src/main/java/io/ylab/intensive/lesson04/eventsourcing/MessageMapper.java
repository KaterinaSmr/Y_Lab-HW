package io.ylab.intensive.lesson04.eventsourcing;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface MessageMapper {

  String wrapPerson(Person person) throws JsonProcessingException;

  String wrapPersonId(Long personId) throws JsonProcessingException;

  Transaction getTransaction(String message) throws JsonProcessingException;

  Person parsePerson(String message) throws JsonProcessingException;

  Long parsePersonId(String message) throws JsonProcessingException;
}
