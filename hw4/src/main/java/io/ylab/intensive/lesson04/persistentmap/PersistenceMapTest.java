package io.ylab.intensive.lesson04.persistentmap;

import java.sql.SQLException;
import javax.sql.DataSource;

import io.ylab.intensive.lesson04.DbUtil;

public class PersistenceMapTest {
  public static void main(String[] args) throws SQLException {
    DataSource dataSource = initDb();
    PersistentMap persistentMap = new PersistentMapImpl(dataSource);

    //предполагаем, что null - валидное значение для ключа и для значения
    //предполагаем, что null - не валидное значение для имени мапы

    //вызов любого метода без инициаизации мапы бросает исключение
    try {
      persistentMap.put("Hello", "World");
    } catch (PersistenceMapMissingNameException e){
      e.printStackTrace();
    }

    persistentMap.init("Languages");
    persistentMap.put("Russia", "russian");
    persistentMap.put("Germany" , "german");

    persistentMap.init("Capitals");
    persistentMap.put("Russia", "Moscow");
    persistentMap.put("France", "Paris");

    //демонстрация метода contains(String key)
    System.out.println("------Demo for contains()------");
    System.out.println("Contains key Russia? " + persistentMap.containsKey("Russia"));
    System.out.println("Contains key Germany? " + persistentMap.containsKey("Germany"));
    System.out.println("Contains key <null>? " + persistentMap.containsKey(null));
    persistentMap.put(null, "test value");
    System.out.println("Contains key <null>? " + persistentMap.containsKey(null));

    //демонстация getKeys()
    System.out.println("------Demo for getKeys()------");
    persistentMap.put("", "test value 1");
    System.out.println("Keys for current map: " + persistentMap.getKeys());
    persistentMap.init("Another");
    System.out.println("Keys for map with no keys: " + persistentMap.getKeys());

    //демонстрация get(String key)
    System.out.println("------Demo for get()------");
    persistentMap.init("Capitals");
    System.out.println("Value for Russia: " + persistentMap.get("Russia"));
    persistentMap.put("key for null value", null);
    //как и в java.util.Map результат null для get() может быть как если нет такого ключа, так и если значение для ключа
    // установлено как null
    System.out.println("Null value in table: " + persistentMap.get("key for null value"));
    System.out.println("Value for non-existing key (Germany): " + persistentMap.get("Germany"));
    System.out.println("Value for <null>: " + persistentMap.get(null));
    System.out.println("Value for \"\": " + persistentMap.get(""));


    //демонстация remove(String key)
    System.out.println("------Demo for remove()------");
    System.out.println("Contains key Russia before remove? " + persistentMap.containsKey("Russia"));
    persistentMap.remove("Russia");
    System.out.println("Contains key Russia after remove? " + persistentMap.containsKey("Russia"));
    //remove for non-existing key
    System.out.println("Contains key Germany before remove? " + persistentMap.containsKey("Germany"));
    persistentMap.remove("Germany");
    System.out.println("Contains key Germany after remove? " + persistentMap.containsKey("Germany"));

    //демонстрация put(String key, String value)
    System.out.println("------Demo for put()------");
    System.out.println("Get for key Russia before put: " + persistentMap.get("Russia"));
    persistentMap.put("Russia", "Moscow");
    System.out.println("Get for key Russia after: " + persistentMap.get("Russia"));
    System.out.println("Get for key <null> before put: " + persistentMap.get(null));
    persistentMap.put(null, "new value for null");
    System.out.println("Get for key <null> after: " + persistentMap.get(null));

    //демонстрация clear()
    System.out.println("------Demo for clear()------");
    System.out.println("Keys before clear() " + persistentMap.getKeys());
    persistentMap.clear();
    System.out.println("Keys after clear() " + persistentMap.getKeys());

  }
  
  public static DataSource initDb() throws SQLException {
    String createMapTable = "" 
                                + "drop table if exists persistent_map; " 
                                + "CREATE TABLE if not exists persistent_map (\n"
                                + "   map_name varchar,\n"
                                + "   KEY varchar,\n"
                                + "   value varchar\n"
                                + ");";
    DataSource dataSource = DbUtil.buildDataSource();
    DbUtil.applyDdl(createMapTable, dataSource);
    return dataSource;
  }
}
