package transliterator;

import java.util.HashMap;
import java.util.Map;

public class TransliteratorImpl implements Transliterator {
    private final Map<Character, String> transliterationRule;

    public TransliteratorImpl() {
        transliterationRule = new HashMap<>();
        transliterationRule.put('А', "A");
        transliterationRule.put('Б', "B");
        transliterationRule.put('В', "V");
        transliterationRule.put('Г', "G");
        transliterationRule.put('Д', "D");
        transliterationRule.put('Е', "E");
        transliterationRule.put('Ё', "E");
        transliterationRule.put('Ж', "ZH");
        transliterationRule.put('З', "Z");
        transliterationRule.put('И', "I");
        transliterationRule.put('Й', "I");
        transliterationRule.put('К', "K");
        transliterationRule.put('Л', "L");
        transliterationRule.put('М', "M");
        transliterationRule.put('Н', "N");
        transliterationRule.put('О', "O");
        transliterationRule.put('П', "P");
        transliterationRule.put('Р', "R");
        transliterationRule.put('С', "S");
        transliterationRule.put('Т', "T");
        transliterationRule.put('У', "U");
        transliterationRule.put('Ф', "F");
        transliterationRule.put('Х', "KH");
        transliterationRule.put('Ц', "TS");
        transliterationRule.put('Ч', "CH");
        transliterationRule.put('Ш', "SH");
        transliterationRule.put('Щ', "SHCH");
        transliterationRule.put('Ъ', "IE");
        transliterationRule.put('Ы', "Y");
        transliterationRule.put('Ь', "");
        transliterationRule.put('Э', "E");
        transliterationRule.put('Ю', "IU");
        transliterationRule.put('Я', "IA");
    }

    @Override
    public String transliterate(String source) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char current = source.charAt(i);
            String s = transliterationRule.get(current);
            if (s == null) {
                sb.append(current);
            } else {
                sb.append(s);
            }
        }
        return sb.toString();
    }
}
