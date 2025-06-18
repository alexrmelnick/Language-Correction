package util;

import java.util.List;
import java.util.Arrays;

public class ArgumentParser {

    private String sentence;
    private String fileName;
    private boolean updateToken;
    private boolean checkSentence;
    private boolean checkFile;
    private boolean updateHashTable;

    ArgumentParser(String[] args) {
        checkSentence = false;
        updateToken = false;
        checkFile  = false;
        updateHashTable = false;
        parseArguments(Arrays.asList(args));
    }

    public static ArgumentParser of(String[] args){
        return new ArgumentParser(args);
    }
    private void parseArguments(List<String> args) {
        
        if(args.size()>0){
            for (int i = 0; i < args.size(); i++) {
                String arg = args.get(i);
                switch (arg) {
                    case "--help":
                        printHelp();
                        break;
                    case "--file":
                        if (i + 1 < args.size()) {
                            fileName = args.get(i + 1);
                            checkFile = true;
                            i++; // Increment to skip the next argument
                        } else {
                            System.err.println("Error: Missing argument after --file");
                        }
                        break;
                    case "--sentence":
                        if (i + 1 < args.size()) {
                            sentence = args.get(i + 1);
                            checkSentence = true;
                            i++; // Increment to skip the next argument
                        } else {
                            System.err.println("Error: Missing argument after --sentence");
                        }
                        break;
                    case "--updateToken":
                        updateToken = true;
                        break;
                    case "--updateHashTable":
                        updateHashTable = true;
                        break;
                    // Add cases for other arguments here
                    default:
                        // Handle unknown arguments or simply ignore them
                        break;
                }
            }
        }else{
            checkSentence = true;
            sentence = "it a very good book, but it is small book.";
            System.out.println("Please enter a sentence. Program used the default sentence:\n" + sentence + "\n--------------------------------------------");
        }
    }


    private void printHelp() {
        System.out.println("Help information:");
        // Add help information here
    }

    // Getters for parsed values
    public String getSentence() {
        return sentence;
    }

    public String getFileName(){
        return fileName;
    }

    public boolean isUpdateToken() {
        return updateToken;
    }
    public boolean isCheckSentence(){
        return checkSentence;
    }
    public boolean isCheckFile(){
        return checkFile;
    }
    public boolean isUpdateHashTable(){
        return updateHashTable;
    }
}
