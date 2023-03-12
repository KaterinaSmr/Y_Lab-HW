package rateLimitedPrinter;

public class RateLimitedPrinter {
    private int interval;
    private long lastPrint;

    public RateLimitedPrinter(int interval) {
        this.interval = interval;
    }

    public void print(String message) {
        if (System.currentTimeMillis() - lastPrint > interval) {
            System.out.println(message);
            lastPrint = System.currentTimeMillis();
//            System.out.println("time: " + lastPrint);
        }
    }
}
