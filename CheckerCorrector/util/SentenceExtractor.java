package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SentenceExtractor {
    private List<String> sentences;

    private SentenceExtractor(List<String> sentences) {
        this.sentences = sentences;
    }

    public static SentenceExtractor of(String filePath) {
        System.out.println(filePath);
        List<String> sentences = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                // Split the line into sentences based on "."
                String[] parts = line.split("\\.");
                for (String part : parts) {
                    // Append the part to the StringBuilder
                    sb.append(part).append(".");
                    // If the StringBuilder contains a complete sentence, add it to the list
                    if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '.') {
                        sentences.add(sb.toString().trim());
                        sb.setLength(0);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SentenceExtractor(sentences);
    }

    public List<String> getSentences() {
        return sentences;
    }
}
