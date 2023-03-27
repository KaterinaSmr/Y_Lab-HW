package io.ylab.intensive.lesson04.filesort;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Validator {
    private final File file;

    public Validator(File file) {
        this.file = file;
    }

    /**
     * Validates if the the file is sorted in descending order and file size is equal to the expected value
     * @param expectedFileSize is expected file size
     * @return true if the file is sorted in descending order and file size is equal to the expected value
     */
    public boolean validate(long expectedFileSize) {
        try (Scanner scanner = new Scanner( new FileInputStream(file))) {
            long prev = Long.MAX_VALUE;
            while (scanner.hasNextLong()) {
                long current = scanner.nextLong();
                if (current > prev) {
                    return false;
                } else {
                    prev = current;
                }
            }
            return file.length() == expectedFileSize;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
