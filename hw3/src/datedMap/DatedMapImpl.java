package datedMap;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class DatedMapImpl implements DatedMap {
    private final HashMap<String, StringWithTimestamp> map;

    public DatedMapImpl() {
        this.map = new HashMap<>();
    }

    static class StringWithTimestamp {
        final String value;
        final Date timestamp;

        public StringWithTimestamp(String value, Date timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "{" + value + " - " + timestamp + "}";
        }
    }

    @Override
    public void put(String key, String value) {
        map.put(key, new StringWithTimestamp(value, new Date()));
    }

    @Override
    public String get(String key) {
        StringWithTimestamp stringWithTimestampFound = map.get(key);
        if (stringWithTimestampFound == null) {
            return null;
        }
        return stringWithTimestampFound.value;
    }

    @Override
    public boolean containsKey(String key) {
        return map.get(key) != null;
    }

    @Override
    public void remove(String key) {
        map.remove(key);
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Date getKeyLastInsertionDate(String key) {
        StringWithTimestamp stringWithTimestampFound = map.get(key);
        if (stringWithTimestampFound == null) {
            return null;
        }
        return stringWithTimestampFound.timestamp;
    }

    @Override
    public String toString() {
        return "DatedMapImpl{" + map + '}';
    }
}
