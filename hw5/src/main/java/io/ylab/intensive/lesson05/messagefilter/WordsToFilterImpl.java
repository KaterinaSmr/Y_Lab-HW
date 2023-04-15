package io.ylab.intensive.lesson05.messagefilter;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for providing a {@link List} of {@link String} objects.
 * Lines are read from file and appened into a {@link List}
 */
@Component
public class WordsToFilterImpl implements WordsToFilter {
  private final File sourceFile = new File("filter.txt");

  @Override
  public List<String> getWordsToFilter() throws IOException {
    List<String> result = new ArrayList<>();
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(sourceFile))) {
      String nextLine;
      while ((nextLine = bufferedReader.readLine()) != null) {
        result.add(nextLine);
      }
      return result;
    }
  }

}
