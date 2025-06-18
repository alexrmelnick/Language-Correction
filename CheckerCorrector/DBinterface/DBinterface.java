package DBinterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import DirectedGraph.DirectedGraph;

import java.sql.*;
import StateMachine.*;
import TypoCorrector.TypoCorrector;
import util.TwoListStruct;


public class DBinterface {
    public int checkTokenInDatabase(String sentence, DirectedGraph<State> graph){
        StateMachine SM = new StateMachine();
        sentence = sentence.replaceAll("\\p{Punct}", " $0");
        String[] tokens = sentence.split("\\s+");
        String url = "jdbc:sqlite:./SQLite/mydatabase.db";
        String dicFileName = "./SQLite/smallDic.txt";
        TypoCorrector typoChecker =  TypoCorrector.of(dicFileName);
        int initialConf = 0;
        try (Connection connection = DriverManager.getConnection(url)) {

            // Lookup each token in the database and categorize it
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                
                try (Statement statement = connection.createStatement()) {
                    
                    String query = "SELECT role FROM word_roles WHERE word = '" + token + "';";
                    String role = new String();
                    
                    ResultSet resultSet = statement.executeQuery(query);
                    if (resultSet.next()) {
                        role = resultSet.getString("role");
                        ////System.out.print("first try: " + token + " -> " + role);
                        tokens[i] = role;
                    }else{
                        String tokenCorrected = new String();
                        if(role.isEmpty()){
                            tokenCorrected = typoChecker.closestWord(token);
                            if(!tokenCorrected.equals(token))
                                initialConf += 5;
                            ////System.out.print("Corrected token: " + token + " -> " + tokenCorrected);
                            query = "SELECT role FROM word_roles WHERE word = '" + tokenCorrected + "';";
                            // Replace the token with its role
                            resultSet = statement.executeQuery(query);
                            if (resultSet.next()) {
                                role = resultSet.getString("role");
                                ////System.out.print("| Second try: "+ token + " -> " + role);
                                tokens[i] = role;
                            }
                        }

                    } 
                    }
                    ////System.out.println();
            }

            List<State> actions = new ArrayList<>();

            for(String token: tokens){
                actions.add(State.fromString(token));
            }
            // Define the initial state
            State initialState = State.START;

            // Check if the sequence of actions follows the state machine

            int confidence = SM.isStateMachineFollowed(graph, actions, initialState, initialConf);
            //System.out.print("The confidence score is: "+ confidence + "\n");
            return confidence;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String correctTokenInDatabase(String sentence, DirectedGraph<State> graph){
        for(int i=0; i<2; i++){
            sentence = new String(correctTokenInDatabaseInnerloop(sentence, graph));
            if(checkTokenInDatabase(sentence, graph)<10)
                break;
        }
        return sentence;
    }

    private String correctTokenInDatabaseInnerloop(String sentence, DirectedGraph<State> graph){
        StateMachine SM = new StateMachine();
        sentence = sentence.replaceAll("\\p{Punct}", " $0");
        String[] tokens = sentence.split("\\s+");
        String[] tokensCopy = tokens.clone();
        List<String> tokenList = new ArrayList<>(Arrays.asList(tokensCopy));
        String url = "jdbc:sqlite:./SQLite/mydatabase.db";
        String dicFileName = "./SQLite/smallDic.txt";
        TypoCorrector typoChecker =  TypoCorrector.of(dicFileName);
        int initialConf = 0;
        try (Connection connection = DriverManager.getConnection(url)) {

            // Lookup each token in the database and categorize it
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                
                try (Statement statement = connection.createStatement()) {
                    
                    String query = "SELECT role FROM word_roles WHERE word = '" + token + "';";
                    String role = new String();
                    
                    ResultSet resultSet = statement.executeQuery(query);
                    if (resultSet.next()) {
                        role = resultSet.getString("role");
                        //////System.out.print("first try: " + token + " -> " + role);
                        tokens[i] = role;
                    }else{
                        String tokenCorrected = new String();
                        if(role.isEmpty()){
                            tokenCorrected = typoChecker.closestWord(token);
                            if(!tokenCorrected.equals(token))
                                initialConf += 5;
                           // ////System.out.print("Corrected token: " + token + " -> " + tokenCorrected);

                            query = "SELECT role FROM word_roles WHERE word = '" + tokenCorrected + "';";
                            // Replace the token with its role
                            resultSet = statement.executeQuery(query);
                            if (resultSet.next()) {
                                tokenList.set(i,tokenCorrected);
                                role = resultSet.getString("role");
                              //  ////System.out.print("| Second try: "+ token + " -> " + role);
                                tokens[i] = role;
                            }
                        }

                    } 
                    }
                    //////System.out.println();
            }

            List<State> actions = new ArrayList<>();

            for(String token: tokens){
                actions.add(State.fromString(token));
            }
            // Define the initial state
            State initialState = State.START;

            // Check if the sequence of actions follows the state machine

            TwoListStruct<State, Integer> output = SM.suggestedStateMachine(graph, actions, initialState);
           // output.displayArrays();
            List<State> suggested = output.getOutputList();
            List<Integer> flags   = output.getChangesList();
            int delCnt = 0;
            boolean seenDot = false;
            int     indDotseen = Math.max(suggested.size()+1, flags.size()+1);
            for(int i=0; i<suggested.size(); i++){
                if(seenDot){
                    //indDotseen = i;
                   // break;
                }
                if(suggested.get(i) == State.DOT)
                    seenDot = true;
                if(flags.get(i+delCnt)==1){
                    try (Statement statement = connection.createStatement()) {
                        String query = "SELECT word FROM word_roles WHERE role = '" + suggested.get(i) + "';";
                        String word = new String();
                        ResultSet resultSet = statement.executeQuery(query);
                        
                        ////System.out.print("!!! 1: " + resultSet + "| ");
                        if (resultSet.next()) {
                            word = resultSet.getString("word");
                            ////System.out.println("Here I am: "+ word);
                            if(i<tokenList.size())
                                tokenList.set(i,word);
                            else
                                tokenList.add(word);
                        }
                    }
                }else if(flags.get(i+delCnt)==2){
                    delCnt++;
                    tokenList.remove(i);
                }else if(flags.get(i+delCnt)==3){
                    try (Statement statement = connection.createStatement()) {
                        ////System.out.println(suggested.get(i));
                        String query = "SELECT word FROM word_roles WHERE role = '" + suggested.get(i) + "';";
                        String word = new String();
                        ResultSet resultSet = statement.executeQuery(query);
                        if (resultSet.next()) {
                            word = resultSet.getString("word");
                            ////System.out.println("Here I am: "+ word);
                            if(i<tokenList.size())
                                tokenList.add(i,word);
                            else
                                tokenList.add(word);
                        }
                    }
                }
            }   
            StringBuilder result = new StringBuilder();
            boolean flagStart = false;
            int i = 0;
            for (String token : tokenList) {
                if(i==indDotseen)
                    break;
                if(flagStart && !token.equals(".") && !token.equals(","))
                    result.append(" ");
                result.append(token);
                flagStart = true;  
                i++;
            }
            return result.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new String();
    }
}
