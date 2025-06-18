package util;
import java.util.ArrayList;
import java.util.List;

public class PhraseExtractor {
    private List<String> phrases;

    private PhraseExtractor(List<String> phrases) {
        this.phrases = phrases;
    }

    public static PhraseExtractor fromSentence(String sentence) {
        List<String> phrases = extractPhrases(sentence, 2, 4);
        return new PhraseExtractor(phrases);
    }

    private PhraseExtractor(List<String> phrases, int start, int end) {
        this.phrases = phrases;
    }

    public static PhraseExtractor fromSentence(String sentence, int start, int end) {
        List<String> phrases = extractPhrases(sentence, start, end);
        return new PhraseExtractor(phrases, start, end);
    }

    private static List<String> extractPhrases(String sentence, int start, int end) {
        List<String> phrases = new ArrayList<>();
        String[] words = sentence.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            for (int j = i + start; (j <= end & j<=words.length); j++){//words.length; j++) {
                StringBuilder phraseBuilder = new StringBuilder();
                for (int k = i; k < j; k++) {
                    phraseBuilder.append(words[k]);
                    if (k < j - 1) {
                        phraseBuilder.append(" ");
                    }
                }
                phrases.add(phraseBuilder.toString());
            }
        }
        return phrases;
    }

    public List<String> getPhrases() {
        return phrases;
    }
}
