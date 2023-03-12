package statsAccumulator;

public class StatsAccumulatorTest {
    public static void main(String[] args) {
        //пустой StatsAccumulator будет выдавать дефолтные значения min, max, avg. Считаем это в рамках допущений
        // как альтернативу, я бы конструктор без арументов сделала приватным и оставила публичным только конструктор
        // с начальным значением. Тогда класс будет более консистентным (но не решилась оставить это основным решением:)
        StatsAccumulator s = new StatsAccumulatorImpl();
        s.add(1);
        s.add(2);
        System.out.println(s.getAvg());
        s.add(0);
        System.out.println(s.getMin());
        s.add(3);
        s.add(8);
        System.out.println(s.getMax());
        System.out.println(s.getCount());
    }
}
