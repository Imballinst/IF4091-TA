/*
 * Copyright 2016 Try.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Try
 */
public final class DBManager {
    private String userName = "root";
    private String password = "";
    private String dbms = "mysql";
    private String serverName = "localhost";
    private String dbName = "testmdl";
    private int portNumber = 3306;
    private Connection conn = null;
    
    /**
     *
     * @throws SQLException
     */
    public DBManager() throws SQLException {
        conn = getConnection();
    }
    
    /**
     *
     * @return
     * @throws SQLException
     */
    private Connection getConnection() throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);

        if (this.dbms.equals("mysql")) {
            conn = DriverManager.getConnection(
                       "jdbc:" + this.dbms + "://" +
                       this.serverName +
                       ":" + this.portNumber + "/" + this.dbName,
                       connectionProps);
        } else if (this.dbms.equals("derby")) {
            conn = DriverManager.getConnection(
                       "jdbc:" + this.dbms + ":" +
                       this.dbName +
                       ";create=true",
                       connectionProps);
        }
        return conn;
    }
    
    /**
     * 
     * @param questionID
     * @return
     * @throws SQLException
     */
    public String getExpectedAnswer(int questionID) throws SQLException {
        PreparedStatement queryStatement = null;
        String stringStatement = "SELECT expectedanswer FROM mdl_qtype_essayinagrader_options WHERE questionid = ?";
        
        ResultSet rs = null;
        String expectedAnswer = "";

        try {
            queryStatement = conn.prepareStatement(stringStatement);
            queryStatement.setInt(1, questionID);
            
            rs = queryStatement.executeQuery();
            
            while (rs.next()) {
                expectedAnswer = rs.getString("expectedanswer");
            }
        }
        catch (SQLException ex){
            
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore
                rs = null;
            }
            if (queryStatement != null) {
                try {
                    queryStatement.close();
                } catch (SQLException sqlEx) { } // ignore
                queryStatement = null;
            }
        }
        
        return expectedAnswer;
    }
    
    /**
     *
     * @param questionID
     * @param questionUsageID
     * @return
     * @throws SQLException
     */
    public String getResponseSummary(int questionID, int questionUsageID) throws SQLException {
        PreparedStatement queryStatement = null;
        String stringStatement = "SELECT responsesummary FROM mdl_question_attempts WHERE questionid = ? AND questionusageid = ?";
        
        ResultSet rs = null;
        String responseSummary = "";

        try {
            queryStatement = conn.prepareStatement(stringStatement);
            queryStatement.setInt(1, questionID);
            queryStatement.setInt(2, questionUsageID);
            
            rs = queryStatement.executeQuery();
            
            while (rs.next()) {
                responseSummary = rs.getString("responesSummary");
            }
        }
        catch (SQLException ex){
            
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore
                rs = null;
            }
            if (queryStatement != null) {
                try {
                    queryStatement.close();
                } catch (SQLException sqlEx) { } // ignore
                queryStatement = null;
            }
        }
        
        return responseSummary;
    }
    
    /**
     *
     * @param word
     * @param pos
     * @return
     */
    public ArrayList<String[]> getSynonyms(String word, String pos) {
        PreparedStatement queryStatement = null;
        String wordStatement = "SELECT wordid FROM mdl_qtype_essayinagrader_synonyms WHERE word = ? AND pos = ?";
        String wordIDStatement = "SELECT word,pos FROM mdl_qtype_essayinagrader_synonyms WHERE wordid = ? AND word != ? AND pos = ?";
        
        ResultSet rs = null;
        int wordID = 0;
        ArrayList<String[]> synonyms = new ArrayList<>();
        
        try {
            // Get wordid from the word
            queryStatement = conn.prepareStatement(wordStatement);
            queryStatement.setString(1, word);
            queryStatement.setString(2, pos);
            
            rs = queryStatement.executeQuery();
            
            while (rs.next()) {
                wordID = rs.getInt("wordid");
            }
        
            // Get words from the wordid
            queryStatement = conn.prepareStatement(wordIDStatement);
            queryStatement.setInt(1, wordID);
            queryStatement.setString(2, word);
            queryStatement.setString(3, pos);
            
            rs = queryStatement.executeQuery();
            
            while (rs.next()) {
                String[] s = new String[2];
                s[0] = rs.getString("word");
                s[1] = rs.getString("pos");
                synonyms.add(s);
            }
        }
        catch (SQLException ex){
            
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore
                rs = null;
            }
            if (queryStatement != null) {
                try {
                    queryStatement.close();
                } catch (SQLException sqlEx) { } // ignore
                queryStatement = null;
            }
        }
        
        return synonyms;
    }
    
    /**
     *
     * @param args
     * @throws SQLException
     */
//    public static void main(String[] args) throws SQLException {
//        DBManager dbm = new DBManager();
//        ArrayList<String[]> str = dbm.getSynonyms("berdaya", "a");
//        for(String[] s : str) {
//            System.out.println(s[0] + " " + s[1]);
//        }
//    }
}
