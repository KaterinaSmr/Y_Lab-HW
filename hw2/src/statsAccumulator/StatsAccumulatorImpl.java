package statsAccumulator;

public class StatsAccumulatorImpl implements StatsAccumulator {
    private int min;
    private int max;
    private int count;
    private Double average;

    StatsAccumulatorImpl() {
       min = Integer.MAX_VALUE;
       max = Integer.MIN_VALUE;
       count = 0;
       average = (double) 0;
    }

    public StatsAccumulatorImpl(int value){
        this();
        add(value);
    }

    @Override
    public void add(int value) {
        count++;
        min = Integer.min(min, value);
        max = Integer.max(max, value);
        average = (average * (count - 1) + value) / count;
    }

    @Override
    public int getMin() {
        return min;
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Double getAvg() {
        return average;
    }

}
