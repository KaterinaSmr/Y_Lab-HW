package transliterator;

public class TransliteratorTest {
    public static void main(String[] args) {
        // Основное решение - с char и циклами. Здесь создается меньше строк.
        Transliterator transliterator = new TransliteratorImpl();
        String result = transliterator.transliterate("HELLO! ПРИВЕТ! Go, boy!");
        System.out.println(result);

        //Альтернативное решение - со стримом строк. Здесь создается больше строк, но читается проще
        Transliterator transliteratorAlternative = new TransliteratorAlternativeImpl();
        String resultAlt = transliteratorAlternative.transliterate("HELLO! ПРИВЕТ! Go, boy!");
        System.out.println(resultAlt);
    }
}
