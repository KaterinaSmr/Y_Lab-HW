package io.ylab.intensive.lesson05.messagefilter;

import java.io.IOException;
import java.util.List;

public interface WordsToFilter {
  List<String> getWordsToFilter() throws IOException;
}
