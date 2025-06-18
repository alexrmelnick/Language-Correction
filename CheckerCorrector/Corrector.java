import java.io.IOException;
import java.util.List;

import DBinterface.DBinterface;
import DirectedGraph.BasicGraph;
import DirectedGraph.DirectedGraph;
import util.ArgumentParser;
import util.JsonMaker;
import util.PhraseExtractor;
import util.SentenceExtractor;
import util.StringFileWriter;
import StateMachine.*;


public class Corrector {
    public static void main(String[] args) {
        //DirectedGraph<State> graph = new DirectedGraph<>();
        
        ArgumentParser argPars = ArgumentParser.of(args);
        BasicGraph basicGraphClass = new BasicGraph();
        DBinterface dbInterface = new DBinterface();
        DirectedGraph<State> graph = basicGraphClass.getGraph();
        StringFileWriter stringWriter = StringFileWriter.of("corrected.txt");

        if(argPars.isCheckFile()){
            SentenceExtractor extractor = SentenceExtractor.of(argPars.getFileName());
            List<String> extractedSentences = extractor.getSentences();  
            
            
            for (String sentence : extractedSentences) {
                System.out.println("Sentence: " + sentence);
                stringWriter.appendString(dbInterface.correctTokenInDatabase(sentence.toLowerCase(), graph));

                try {
                    stringWriter.writeToFile();
                    System.out.println("Corrected version has been written to the file.");
                } catch (IOException e) {
                    System.err.println("An error occurred while writing to the file: " + e.getMessage());
                }
                System.out.println("##########################################################");
                
            }
        }else if(argPars.isCheckSentence()){

            System.out.println("Sentence: " + argPars.getSentence());
            stringWriter.appendString(dbInterface.correctTokenInDatabase(argPars.getSentence().toLowerCase(), graph));
            try {
                stringWriter.writeToFile();
                System.out.println("Corrected version has been written to the file.");
            } catch (IOException e) {
                System.err.println("An error occurred while writing to the file: " + e.getMessage());
            }
            System.out.println("##########################################################");
        }
          
    }
}
