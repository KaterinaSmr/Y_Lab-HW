
public class MultTable {
    static final int MAX = 9;
    public static void main(String[] args) throws Exception {
        printMultTable();
    }

    public static void printMultTable(){
        for (int i = 1; i <= MAX ; i++) {
            for (int j = 1; j <= MAX; j++) {
                System.out.println(i + " x " + j + " = " + (i * j));
            }
        }
    }

}

