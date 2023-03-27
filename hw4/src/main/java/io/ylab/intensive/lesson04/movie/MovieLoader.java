package io.ylab.intensive.lesson04.movie;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public interface MovieLoader {
  void loadData(File file) throws IOException, SQLException;
}
