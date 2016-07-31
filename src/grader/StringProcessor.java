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

import java.sql.SQLException;
import java.util.ArrayList;

import IndonesianNLP.IndonesianNETagger;
import IndonesianNLP.IndonesianPOSTagger;
import IndonesianNLP.IndonesianPhraseChunker;
import IndonesianNLP.IndonesianSentenceFormalization;
import IndonesianNLP.IndonesianStemmer;
import IndonesianNLP.IndonesianSentenceDetector;

/**
 *
 * @author Try
 */
public class StringProcessor {

    /**
     *
     * @param word
     * @return
     */
    public String processStem(String word) {
        IndonesianStemmer stemmer = new IndonesianStemmer();
        String str = stemmer.stemSentence(word);

        return str;
    //        for(String s : str2) {    
    //          System.out.println(s);
    //        }
    }
    
    /**
     *
     * @param sentence
     * @return
     */
    public ArrayList<String[]> processPOSTag(String sentence) {
        ArrayList<String[]> str = IndonesianPOSTagger.doPOSTag(sentence);
        
        return str;
//        for(int i=0; i<str.size(); i++)
//            System.out.println(str.get(i)[0]+"/"+str.get(i)[1]);
    }
    
    /**
     *
     * @param sentence
     * @return
     */
    public ArrayList<String[]> processChunkSentence(String sentence) {
        IndonesianPhraseChunker chunker = new IndonesianPhraseChunker();
        ArrayList<String[]> str = chunker.doPhraseChunker(sentence);
        
        return str;
//        for(int i=0; i<str.size(); i++)
//            System.out.println(str.get(i)[0]+"/"+str.get(i)[1]);
    }
    
    /**
     *
     * @param sentence
     * @return
     */
    public ArrayList<String[]> processNETagSentence(String sentence) {
        IndonesianNETagger NETagger = new IndonesianNETagger();
        ArrayList<String[]> str = NETagger.NETagLine(sentence);
        
        return str;
//        for(int i=0; i<str.size(); i++)
//            System.out.println(str.get(i)[0]+"/"+str.get(i)[1]);
    }
    
    /**
     *
     * @param sentence
     * @return
     */
    public ArrayList<String> processSplitSentence(String sentence) {
        IndonesianSentenceDetector sentenceDetector = new IndonesianSentenceDetector();
        ArrayList<String> str = sentenceDetector.splitSentence(sentence);
        
        return str;
//        for(String s : str2) {    
//          System.out.println(s);
//        }
    }
    
    /**
     *
     * @param sentence
     * @return
     */
    public String processFormalizeSentence(String sentence) {
        IndonesianSentenceFormalization formalizer = new IndonesianSentenceFormalization();
        String str = formalizer.formalizeSentence(sentence);
        formalizer.initStopword();
        
        return formalizer.deleteStopword(str);
    }
    
    /**
     *
     * @param arr
     * @return
     */
    public ArrayList<String[]> removeUnimportantWords(ArrayList<String[]> arr) {
        ArrayList<String[]> filteredWords = new ArrayList<>();
        
        // Remove UH, IN, and CC POS tag
        for(int i = 0; i < arr.size(); i++) {
            if (arr.get(i)[1].compareTo("UH") != 0 &&
                arr.get(i)[1].compareTo("IN") != 0 &&
                arr.get(i)[1].compareTo("CC") != 0) {
                // Check duplicates
                if (!containsWord(filteredWords, arr.get(i))) {
                    // Add to new ArrayList
                    filteredWords.add(arr.get(i));
                }
            }
        }
        
        return filteredWords;
    }
    
    /**
     *
     * @param arr
     * @param word
     * @return
     */
    public boolean containsWord(ArrayList<String[]> arr, String[] word) {
        // true if the ArrayList contains the wordlist already
        boolean containsWord = false;
        int idx = 0;
        
        while (idx < arr.size() && !containsWord) {
            if (isSame(arr.get(idx), word)) {
                containsWord = true;
            }
            idx++;
        }
        
        return containsWord;
    }
    
    /**
     *
     * @param str1
     * @param str2
     * @return
     */
    public boolean isSame(String[] str1, String[] str2) {
        return (str1[0].toLowerCase().compareTo(str2[0].toLowerCase()) == 0 && 
                str1[1].toLowerCase().compareTo(str2[1].toLowerCase()) == 0);
    }
    
    /**
     *
     * @param posTag1
     * @param posTag2
     * @return
     */
    public boolean isPOSTagSame(String posTag1, String posTag2) {
        boolean isSame = true;
        int charIdx = 0;
        int maxIdx = 2; // POS tag longer than 2 characters are just more specific
        
        while (isSame && charIdx < maxIdx) {
            if(posTag1.charAt(charIdx) != posTag2.charAt(charIdx)) {
                isSame = false;
            }
            charIdx++;
        }
        
        return isSame;
    }
    
    /**
     *
     * @param s
     * @return
     */
    public int countWordsBySpaces(String s) {
        return s.split(" ").length;
    }
    
    /**
     *
     * @param _realAnswer
     * @param _userAnswer
     * @return
     */
    public ChunkerSimilarityOutput getFirstSimilarity (String _realAnswer, String _userAnswer) {
        // Preprocess
        ArrayList<String[]> realAnswer = processChunkSentence(_realAnswer),
                            userAnswer = processChunkSentence(_userAnswer);
        
        // Chunk tag comparison
        ChunkerSimilarityOutput chunkerSimilarity = new ChunkerSimilarityOutput();
        int i = 0, j; //index
        int countSame = 0;
        int defaultComparatorSize = realAnswer.size();
        
        while (i < realAnswer.size()) {
            boolean resetCycle = false;
            j = 0;
            while (j < userAnswer.size() && !resetCycle) {
                if (isSame(realAnswer.get(i), userAnswer.get(j))) {
                    realAnswer.remove(i);
                    userAnswer.remove(j);
                    i--;
                    j--;
                    resetCycle = true;
                    countSame++;
                }
                j++;
            }
            i++;
        }
        
        chunkerSimilarity.setRealAnswer(realAnswer);
        chunkerSimilarity.setUserAnswer(userAnswer);
        chunkerSimilarity.setSimilarityPercentage(chunkerSimilarity.getSimilarityPercentage() + ((double) countSame / (double) defaultComparatorSize));
        chunkerSimilarity.setBaseChunkerSize(defaultComparatorSize);
        
        return chunkerSimilarity;
    }
    
    /**
     *
     * @param chunkerSimilarityOutput
     * @return
     */
    public ChunkerSimilarityOutput getSecondSimilarity (ChunkerSimilarityOutput chunkerSimilarityOutput) {
        ChunkerSimilarityOutput chunkerSimilarity = new ChunkerSimilarityOutput(chunkerSimilarityOutput);
        ArrayList<String[]> userAnswer = chunkerSimilarityOutput.getUserAnswer(), 
                            realAnswer = chunkerSimilarityOutput.getRealAnswer();
        String baseRealAnswer = "", baseUserAnswer = "";
        
        // Alter the ArrayList of String[] to a String, so then it can be POS tagged
        for (int i = 0; i < realAnswer.size(); i++) {
            baseRealAnswer += realAnswer.get(i)[0];
            if (i + 1 == realAnswer.size()) {
                // Remove whitespace at the end
                baseRealAnswer = baseRealAnswer.substring(0, baseRealAnswer.length()-1);
            }
        }
        for (int j = 0; j < userAnswer.size(); j++) {
            baseUserAnswer += userAnswer.get(j)[0];
            if (j + 1 == userAnswer.size()) {
                // Remove whitespace at the end
                baseUserAnswer = baseUserAnswer.substring(0, baseUserAnswer.length()-1);
            }
        }
        
        // Replace chunkerSimilarityOutput from Chunk tag to POS tag
        chunkerSimilarityOutput.setUserAnswer(processPOSTag(baseUserAnswer));
        chunkerSimilarityOutput.setRealAnswer(processPOSTag(baseRealAnswer));
        chunkerSimilarityOutput.setBaseChunkerSize(processPOSTag(baseUserAnswer).size());
        
        // Redefine
        userAnswer = chunkerSimilarityOutput.getUserAnswer();
        realAnswer = chunkerSimilarityOutput.getRealAnswer();
        
        // POS tag comparison
        int i = 0, j; //index
        int countSame = 0;
        int defaultComparatorSize = realAnswer.size();
        
        while (i < realAnswer.size()) {
            boolean resetCycle = false;
            j = 0;
            while (j < userAnswer.size() && !resetCycle) {
                if (isSame(realAnswer.get(i), userAnswer.get(j))) {
                    realAnswer.remove(i);
                    userAnswer.remove(j);
                    i--;
                    j--;
                    resetCycle = true;
                    countSame++;
                }
                j++;
            }
            i++;
        }
        
        chunkerSimilarity.setRealAnswer(realAnswer);
        chunkerSimilarity.setUserAnswer(userAnswer);
        chunkerSimilarity.setSimilarityPercentage(chunkerSimilarity.getSimilarityPercentage() + ((double) countSame / (double) defaultComparatorSize));
        chunkerSimilarity.setBaseChunkerSize(defaultComparatorSize);
        
        return chunkerSimilarity;
    }
    
    /**
     *
     * @param chunkerSimilarityOutput
     * @return
     * @throws SQLException
     */
    public ChunkerSimilarityOutput getThirdSimilarity (ChunkerSimilarityOutput chunkerSimilarityOutput) throws SQLException {
        // POS Tag comparison
        ChunkerSimilarityOutput chunkerSimilarity = new ChunkerSimilarityOutput(chunkerSimilarityOutput);
        ArrayList<String[]> userAnswer = chunkerSimilarityOutput.getUserAnswer(), 
                            realAnswer = chunkerSimilarityOutput.getRealAnswer(),
                            synonyms;
        String[] nearestString;
        Double nearestStringValue;
        DBManager dbm = new DBManager();
        NearestSynonym ns = new NearestSynonym();
        JaroWinkler jw = new JaroWinkler();
        
        int i = 0, j, k; //index
        int countSame = 0;
        int defaultComparatorSize = chunkerSimilarityOutput.getBaseChunkerSize();
        
        while (i < realAnswer.size()) {
            boolean resetCycle = false;
            j = 0;
            while (j < userAnswer.size() && !resetCycle) {
                // Get synonyms from word and pos
                k = 0;
                nearestStringValue = 0d;
                nearestString = new String[2];
                synonyms = dbm.getSynonyms(userAnswer.get(j)[0], userAnswer.get(j)[1]);
                while (k < synonyms.size() && !resetCycle) {
                    // Compare i with k, because i and j were compared already
                    if (isSame(realAnswer.get(i), synonyms.get(k))) {
                        realAnswer.remove(i);
                        userAnswer.remove(j);
                        i--;
                        j--;
                        resetCycle = true;
                        countSame++;
                    } else {
                        Double temp = jw.apply(realAnswer.get(i)[0], 
                                               synonyms.get(k)[0]);
                        
                        if (temp > nearestStringValue) {
                            nearestString[0] = realAnswer.get(i)[0];
                            nearestString[1] = synonyms.get(k)[0];
                            nearestStringValue = temp;
                        }
                    }
                    k++;
                }
                
                // Check if there is a same word
                if (!resetCycle) {
                    ns.addNearestSynonymString(nearestString);
                    ns.addNearestSynonymPercentage(nearestStringValue);
                }
                j++;
            }
            i++;
        }
        
        chunkerSimilarity.setRealAnswer(realAnswer);
        chunkerSimilarity.setUserAnswer(userAnswer);
        chunkerSimilarity.setSimilarityPercentage(chunkerSimilarity.getSimilarityPercentage() + ((double) countSame / (double) defaultComparatorSize));
        chunkerSimilarity.setNearestSynonym(ns);
        
        return chunkerSimilarity;
    }
    
    /**
     *
     * @param chunkerSimilarityOutput
     * @return
     */
    public ChunkerSimilarityOutput getFourthSimilarity (ChunkerSimilarityOutput chunkerSimilarityOutput) {
        for(int i = 0; i < chunkerSimilarityOutput.getNearestSynonym().getNearestSynonymString().size(); i++) {
            System.out.println(chunkerSimilarityOutput.getNearestSynonym().getNearestSynonymString()
                    .get(i)[0] + " " + chunkerSimilarityOutput.getNearestSynonym().getNearestSynonymString()
                    .get(i)[1] + " " + chunkerSimilarityOutput.getNearestSynonym().
                    getNearestSynonymPercentage().get(i));
        }
        
        // Jaro Winkler
        ChunkerSimilarityOutput chunkerSimilarity = new ChunkerSimilarityOutput(chunkerSimilarityOutput);
        ArrayList<String[]> userAnswer = chunkerSimilarityOutput.getUserAnswer(), 
                            realAnswer = chunkerSimilarityOutput.getRealAnswer();
        JaroWinkler jaroWinkler = new JaroWinkler();
        int defaultComparatorSize = chunkerSimilarityOutput.getBaseChunkerSize();
        
        String concatSentence = "", concatSentenceComparison = "";
        for (int i = 0; i < realAnswer.size(); i++) {
            concatSentence += realAnswer.get(i)[0];
            if (i + 1 < realAnswer.size()) {
                // Add whitespace
                concatSentence += " ";
            }
        }
        for (int i = 0; i < userAnswer.size(); i++) {
            concatSentenceComparison += userAnswer.get(i)[0];
            if (i + 1 < userAnswer.size()) {
                // Add whitespace
                concatSentenceComparison += " ";
            }
        }
        
        System.out.println(concatSentence);
        System.out.println(concatSentenceComparison);
        System.out.println((jaroWinkler.apply(concatSentence, concatSentenceComparison)));
        
        chunkerSimilarity.setSimilarityPercentage(chunkerSimilarity.getSimilarityPercentage() + (jaroWinkler.apply(concatSentence, concatSentenceComparison) / (double)defaultComparatorSize));
        
        return chunkerSimilarity;
    }
    
    /**
     *
     * @param base
     * @param compare
     * @throws SQLException
     */
    public void compareSentence(String base, String compare) throws SQLException {
        if (countWordsBySpaces(base) > 1 && countWordsBySpaces(compare) > 1) {
            // more than one
            ChunkerSimilarityOutput sim1, sim2, sim3, sim4;
        
            sim1 = getFirstSimilarity(base, compare);
            sim2 = getSecondSimilarity(sim1);
            sim3 = getThirdSimilarity(sim2);
            sim4 = getFourthSimilarity(sim3);
            System.out.println(sim3.getSimilarityPercentage());
        } else {
            JaroWinkler jw = new JaroWinkler();
            System.out.println("JW: " + jw.apply(base, compare));
        }
    }
    
    /**
     *
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {
        StringProcessor sp = new StringProcessor();
        String s = "Aku tidak pergi";
        String t = "Aku pergi ke Institut Teknologi Bandung";
        
        sp.compareSentence(s, t);
    }
}