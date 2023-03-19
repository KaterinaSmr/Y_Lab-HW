package fileSort;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class Test {
    public static void main(String[] args) throws IOException {
        File dataFile = new Generator().generate("data.txt", 100);
        System.out.println(new Validator(dataFile).isSorted());
        File sortedFile = new Sorter().sortFile(dataFile);
        System.out.println(new Validator(sortedFile).isSorted());
        System.out.println("Same length? " + (sortedFile.length() == dataFile.length()));

//        testMax();
    }

    //У меня заняло 34 мин
    public static void testMax() throws IOException{
        System.out.println("START: " + LocalDateTime.now());
        File dataFile = new Generator().generate("data.txt", 375_000_000);
        System.out.println(new Validator(dataFile).isSorted());
        File sortedFile = new Sorter(5_000_000, 500_000).sortFile(dataFile);
        System.out.println(new Validator(sortedFile).isSorted());
        System.out.println("Same length? " + (sortedFile.length() == dataFile.length()));
        System.out.println("END: " + LocalDateTime.now());
    }
}
