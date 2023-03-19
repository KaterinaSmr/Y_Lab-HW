package datedMap;

public class DatedMapTest {
    public static void main(String[] args) {
        DatedMap datedMap = new DatedMapImpl();
        datedMap.put("Russia", "Moscow");
        datedMap.put("France", "Paris");
        datedMap.put("Germany", "Berlin");
        System.out.println(datedMap);

        System.out.println("Value for Russia = " + datedMap.get("Russia"));
        System.out.println("Contains key France? " + datedMap.containsKey("France"));
        System.out.println("KeyLastInsertionDate for key \"Russia\" = " + datedMap.getKeyLastInsertionDate("Russia"));
        datedMap.remove("France");
        System.out.println("Key set = " + datedMap.keySet());
    }
}
