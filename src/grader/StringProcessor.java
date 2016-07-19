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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Try
 */
public class StringProcessor {

    /**
     *
     * @param sentence
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public ArrayList<String[]> generatePOSTag(String sentence) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
        // Reflection
        Class fooClass = Class.forName("IndonesianPOSTagger");
        Method fooMethod =
            fooClass.getMethod("doPOSTag", new Class[] { String.class });

        ArrayList<String[]> str =
            (ArrayList<String[]>) fooMethod.invoke(fooClass.newInstance(), sentence);
        
        return str;
//        for(int i=0; i<str.size(); i++)
//            System.out.println(str.get(i)[0]+"/"+str.get(i)[1]);
    }
    
    /**
     *
     * @param sentence
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public ArrayList<String> splitSentences(String sentence) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
        // Reflection
        Class fooClass = Class.forName("IndonesianSentenceDetector");
        Method fooMethod =
            fooClass.getMethod("splitSentence", new Class[] { String.class });

        ArrayList<String> str =
            (ArrayList<String>) fooMethod.invoke(fooClass.newInstance(), sentence);
        
        return str;
//        for(String s : str2) {    
//          System.out.println(s);
//        }
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
        return (str1[0].compareTo(str2[0]) == 0 && str1[1].compareTo(str2[1]) == 0);
    }
    
    public boolean isSynonymSame(String[] str1, String[] str2) {
        return (str1[0].compareTo(str2[0]) == 0 && str1[1].compareTo(str2[1]) == 0);
    }
    
    public boolean isPOSTagSame(String posTag1, String posTag2) {
        boolean isSame = false;
        
        
        
        return isSame;
    }
    
    /**
     *
     * @param sentenceWithPOSTag
     * @param sentenceComparison
     * @return
     */
    public SimilarityOutput getFirstSimilarity (ArrayList<String[]> sentenceWithPOSTag, ArrayList<String[]> sentenceComparison) {
        // POS Tag comparison
        SimilarityOutput similarity = new SimilarityOutput();
        int i = 0, j = 0; //index
        int countSame = 0;
        int defaultComparatorSize = sentenceComparison.size();
        
        while (i < sentenceWithPOSTag.size()) {
            boolean resetCycle = false;
            while (j < sentenceComparison.size() && !resetCycle) {
                if (isSame(sentenceWithPOSTag.get(i), sentenceComparison.get(j))) {
                    sentenceWithPOSTag.remove(i);
                    sentenceComparison.remove(j);
                    i--;
                    j--;
                    resetCycle = true;
                    countSame++;
                }
                j++;
            }
            i++;
        }
        
        similarity.filteredSentenceWithPOSTag = new ArrayList<>(sentenceWithPOSTag);
        similarity.sentenceComparison = new ArrayList<>(sentenceComparison);
        similarity.similarityPercentage += ((double) countSame / (double) defaultComparatorSize);
        similarity.baseSentenceSize = defaultComparatorSize;
        
        return similarity;
    }
    
    /**
     *
     * @param similarityOutput
     * @return
     * @throws SQLException
     */
    public SimilarityOutput getSecondSimilarity (SimilarityOutput similarityOutput) throws SQLException {
        // POS Tag comparison
        SimilarityOutput similarity = new SimilarityOutput(similarityOutput);
        ArrayList<String[]> sentenceComparison = similarityOutput.sentenceComparison, 
                            sentenceWithPOSTag = similarityOutput.filteredSentenceWithPOSTag,
                            synonyms;
        DBManager dbm = new DBManager();
        
        int i = 0, j = 0, k; //index
        int countSame = 0;
        int defaultComparatorSize = similarityOutput.baseSentenceSize;
        
        while (i < sentenceWithPOSTag.size()) {
            boolean resetCycle = false;
            while (j < sentenceComparison.size() && !resetCycle) {
                // Get synonyms from word and pos
                k = 0;
                synonyms = dbm.getSynonyms(sentenceComparison.get(j)[0], sentenceComparison.get(j)[1]);
                while (k < synonyms.size() && !resetCycle) {
                    // Compare i with k, because i and j were compared already
                    System.out.println(sentenceWithPOSTag.get(i) + " " + synonyms.get(k));
                    if (isSame(sentenceWithPOSTag.get(i), synonyms.get(k))) {
                        sentenceWithPOSTag.remove(i);
                        sentenceComparison.remove(j);
                        i--;
                        j--;
                        resetCycle = true;
                        countSame++;
                    }
                    k++;
                }
                j++;
            }
            i++;
        }
        
        similarity.filteredSentenceWithPOSTag = new ArrayList<>(sentenceWithPOSTag);
        similarity.sentenceComparison = new ArrayList<>(sentenceComparison);
        similarity.similarityPercentage += ((double)countSame / (double)defaultComparatorSize);
        
        return similarity;
    }
    
    /**
     *
     * @param similarityOutput
     * @return
     */
    public SimilarityOutput getThirdSimilarity (SimilarityOutput similarityOutput) {
        // Jaro Winkler
        SimilarityOutput similarity = new SimilarityOutput(similarityOutput);
        ArrayList<String[]> sentenceComparison = similarityOutput.sentenceComparison, 
                            sentenceWithPOSTag = similarityOutput.filteredSentenceWithPOSTag;
        JaroWinkler jaroWinkler = new JaroWinkler();
        int defaultComparatorSize = similarityOutput.baseSentenceSize;
        
        String concatSentence = "", concatSentenceComparison = "";
        for (int i = 0; i < sentenceWithPOSTag.size(); i++) {
            concatSentence += " " + sentenceWithPOSTag.get(i)[0];
        }
        for (int i = 0; i < sentenceComparison.size(); i++) {
            concatSentenceComparison += " " + sentenceComparison.get(i)[0];
        }
        
        similarity.similarityPercentage += (jaroWinkler.apply(concatSentence, concatSentenceComparison) / (double)defaultComparatorSize);
        
        return similarity;
    }
    
    /**
     *
     * @param base
     * @param compare
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws SQLException
     */
    public void processWords(String base, String compare) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, SQLException {
        SimilarityOutput sim1, sim2, sim3;
        ArrayList<String[]> sentenceBase = generatePOSTag(base),
                            sentenceCompare = generatePOSTag(compare);
        sim1 = getFirstSimilarity(sentenceBase, sentenceCompare);
        sim2 = getSecondSimilarity(sim1);
        sim3 = getThirdSimilarity(sim2);
        System.out.println(sim3.similarityPercentage);
    }
    
    /**
     *
     * @param args
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws SQLException
     */
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, SQLException {
        StringProcessor sp = new StringProcessor();
        String s = "Saya layak";
        String t = "Saya mahir";
        
        sp.processWords(s, t);
//        ArrayList<String[]> s2 = sp.generatePOSTag(s);
//        ArrayList<String[]> s3 = sp.removeUnimportantWords(s2);
//        ArrayList<String[]> t2 = sp.generatePOSTag(t);
//        ArrayList<String[]> t3 = sp.removeUnimportantWords(t2);
//        System.out.println(sp.getFirstSimilarity(s3, t3).similarityPercentage);

//          JaroWinkler j = new JaroWinkler();
//          String s = "ini kebodohan adalah";
//          String s2 = "ini adalah kebodohan";
//          System.out.println(j.apply(s,s2));
    }
}