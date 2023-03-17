package rateLimitedPrinter;

public class RateLimitedPrinterTest {

    public static void main(String[] args) {
        //В чате было сообщение, что здесь можно без интерфейса, поэтому без него
        RateLimitedPrinter rateLimitedPrinter = new RateLimitedPrinter(1000);
        for (int i = 0; i < 1_000_000_000; i++) {
            rateLimitedPrinter.print(String.valueOf(i));
        }
    }
}
