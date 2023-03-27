package io.ylab.intensive.lesson04.persistentmap;

public class PersistenceMapMissingNameException extends RuntimeException {
  public PersistenceMapMissingNameException() {
    super("Map name cannot be null!");
  }
}
