package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class StringFileWriter {
    private StringBuilder stringBuilder;
    private String filePath;
    private String space;

    private StringFileWriter(String filePath) {
        this.stringBuilder = new StringBuilder();
        this.filePath = filePath;
        this.space = " ";
    }
    private StringFileWriter(String filePath, String S) {
        this.stringBuilder = new StringBuilder();
        this.filePath = filePath;
        this.space = S;
    }

    public static StringFileWriter of(String filePath, String S) {
        return new StringFileWriter(filePath, S);
    }

    public static StringFileWriter of(String filePath) {
        return new StringFileWriter(filePath);
    }

    public void appendString(String str) {
        stringBuilder.append(str);
        stringBuilder.append(space);
    }

    public void writeToFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(stringBuilder.toString());
        }
    }
}
