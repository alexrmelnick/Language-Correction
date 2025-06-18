package HashTableMaker;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;

import util.PhraseExtractor;

public class HashTableMaker {
    private Connection connection;

    public HashTableMaker() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:SQLite/hash_database.db");
        createTableIfNotExists();
    }

    private void createTableIfNotExists() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS hashes (hash TEXT PRIMARY KEY, count INTEGER)");
        }
    }


    public HashMap<String, Integer> loadHashedSentences() throws SQLException {
        HashMap<String, Integer> hashedSentencesMap = new HashMap<>();

        String query = "SELECT hash, count FROM hashes";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String hash = resultSet.getString("hash");
                int count = resultSet.getInt("count");
                hashedSentencesMap.put(hash, count);
            }
        }

        return hashedSentencesMap;
    }

    public void updateDatabase(String phrase) throws SQLException, NoSuchAlgorithmException {
        String hash = generateHash(phrase);

        PreparedStatement selectStatement = connection.prepareStatement("SELECT count FROM hashes WHERE hash = ?");
        selectStatement.setString(1, hash);
        ResultSet resultSet = selectStatement.executeQuery();

        if (resultSet.next()) {
            int count = resultSet.getInt("count");
            PreparedStatement updateStatement = connection.prepareStatement("UPDATE hashes SET count = ? WHERE hash = ?");
            updateStatement.setInt(1, count + 1);
            updateStatement.setString(2, hash);
            updateStatement.executeUpdate();
        } else {
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO hashes (hash, count) VALUES (?, ?)");
            insertStatement.setString(1, hash);
            insertStatement.setInt(2, 1);
            insertStatement.executeUpdate();
        }
    }

    private String generateHash(String phrase) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(phrase.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public int getTotalCount() throws SQLException {
        int totalCount = 0;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT SUM(count) AS total FROM hashes")) {
            if (resultSet.next()) {
                totalCount = resultSet.getInt("total");
            }
        }

        return totalCount;
    }

    public int getCountForSentence(String sentence) throws SQLException, NoSuchAlgorithmException {
        String hash = generateHash(sentence);

        PreparedStatement selectStatement = connection.prepareStatement("SELECT count FROM hashes WHERE hash = ?");
        selectStatement.setString(1, hash);
        ResultSet resultSet = selectStatement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("count");
        } else {
            return 0;
        }
    }

    public int nGram(String sentence, int n){
        String[] words = sentence.split("\\s+");
        if(words.length<n)
            return -1;
        PhraseExtractor extractorPhraseN1 = PhraseExtractor.fromSentence(sentence, n-2, n-1);
        PhraseExtractor extractorPhraseN = PhraseExtractor.fromSentence(sentence, n-1, n);
        List<String> phrasesN = extractorPhraseN.getPhrases();
        List<String> phrasesN1 = extractorPhraseN1.getPhrases();
        int cntCurN = 0;
        int cntCurN1 = 0;
        try{
            for(String phrase: phrasesN){
                cntCurN += getCountForSentence(phrase);
            }
            for(String phrase: phrasesN1){
                cntCurN1 += getCountForSentence(phrase);
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if(cntCurN1==0)
            return 0;
        double p = ((double)cntCurN)/((double)cntCurN1);
        return 100 - (int)p;
    }
}
