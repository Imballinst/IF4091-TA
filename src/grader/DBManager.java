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
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Try
 */
public class DBManager {
    String userName = "root";
    String password = "";
    String dbms = "mysql";
    String serverName = "localhost";
    String dbName = "moodle";
    int portNumber = 3306;
    Connection conn = null;
    
    public DBManager() throws SQLException {
        conn = getConnection();
    }
    
    public Connection getConnection() throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);

        if (this.dbms.equals("mysql")) {
            conn = DriverManager.getConnection(
                       "jdbc:" + this.dbms + "://" +
                       this.serverName +
                       ":" + this.portNumber + "/",
                       connectionProps);
        } else if (this.dbms.equals("derby")) {
            conn = DriverManager.getConnection(
                       "jdbc:" + this.dbms + ":" +
                       this.dbName +
                       ";create=true",
                       connectionProps);
        }
        System.out.println("Connected to database");
        return conn;
    }
    
    public String getExpectedAnswer(int quizID, int questionID) throws SQLException {
        PreparedStatement queryStatement = null;
        String stringStatement = "SELECT expectedanswer FROM mdl_qtype_essayinagrader_options WHERE expectedanswer = ?";
        
        Statement stmt = null;
        ResultSet rs = null;
        String answer = "";

        try {
            conn.setAutoCommit(false);
            queryStatement = conn.prepareStatement(stringStatement);
            queryStatement.setInt(1, quizID);
            queryStatement.setInt(2, questionID);
            
            rs = queryStatement.executeQuery();
            
            while (rs.next()) {
                answer = rs.getString("expectedanswer");
            }
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore
                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore
                stmt = null;
            }
        }
        
        return answer;
    }
    
    public ArrayList<String> getSynonyms(String word, char pos) {
        PreparedStatement queryStatement = null;
        String wordIDStatement = "SELECT wordid FROM mdl_qtype_essayinagrader_options WHERE word = ?";
        String stringStatement = "SELECT word FROM mdl_qtype_essayinagrader_options WHERE word = ?";
        
        Statement stmt = null;
        ResultSet rs = null;
        String wordPos = word.concat("-" + pos);
        ArrayList<String> synonyms = new ArrayList<>();

        try {
            conn.setAutoCommit(false);
            queryStatement = conn.prepareStatement(stringStatement);
            queryStatement.setString(1, wordPos);
            
            rs = queryStatement.executeQuery();
            
            while (rs.next()) {
                synonyms.add(rs.getString("word"));
            }
        }
        catch (SQLException ex){
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore
                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore
                stmt = null;
            }
        }
        
        return synonyms;
    }
}
