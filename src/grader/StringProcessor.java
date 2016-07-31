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

import IndonesianNLP.IndonesianPOSTagger;
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
    }
    
    /**
     *
     * @param sentence
     * @return
     */
    public ArrayList<String[]> processPOSTag(String sentence) {
        ArrayList<String[]> str = IndonesianPOSTagger.doPOSTag(sentence);
        
        return str;
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
    }
    
    /**
     *
     * @param sentence
     * @return
     */
    public String processFormalizeSentence(String sentence) {
        IndonesianSentenceFormalization formalizer = new IndonesianSentenceFormalization();
        String str = formalizer.formalizeSentence(sentence);
        
        return str;
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
    public SimilarityOutput getFirstSimilarity (String _realAnswer, String _userAnswer) {
        // Preprocess
        ArrayList<String[]> realAnswer = processPOSTag(_realAnswer),
                            userAnswer = processPOSTag(_userAnswer);
        
        // Chunk tag comparison
        SimilarityOutput similarity = new SimilarityOutput();
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
        
        if (userAnswer.size() > realAnswer.size()) {
            similarity.setExcessivePOSWord(userAnswer.size() - realAnswer.size());
        }
        System.out.println(countSame);
        System.out.println("Excessive: " + similarity.getExcessivePOSWord());
        
        similarity.setRealAnswer(realAnswer);
        similarity.setUserAnswer(userAnswer);
        similarity.setBasePOSTagCountSame(countSame);
        similarity.setBasePOSTagSize(defaultComparatorSize);
        similarity.setSimilarityPercentage(similarity.getSimilarityPercentage() + ((double) countSame / (double) defaultComparatorSize));
        
        System.out.println("***");        
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
        ArrayList<String[]> userAnswer = similarityOutput.getUserAnswer(), 
                            realAnswer = similarityOutput.getRealAnswer(),
                            synonyms;
        String[] nearestString;
        Double nearestStringValue;
        DBManager dbm = new DBManager();
        NearestSynonym ns = new NearestSynonym();
        JaroWinkler jw = new JaroWinkler();
        
        System.out.println(similarity.getBasePOSTagCountSame());
        System.out.println("Excessive: " + similarity.getExcessivePOSWord());
        
        if (!userAnswer.isEmpty() && !realAnswer.isEmpty()) {
            int i = 0, j, k; //index
            int countSame = 0;
            int defaultComparatorSize = similarityOutput.getBasePOSTagSize();
            
            while (i < realAnswer.size()) {
                boolean resetCycle = false;
                j = 0;
                while (j < userAnswer.size() && !resetCycle) {
                    // Get synonyms from word and pos
                    k = 0;
                    nearestStringValue = 0d;
                    nearestString = new String[2];
                    synonyms = dbm.getSynonyms(userAnswer.get(j)[0], userAnswer.get(j)[1]);
                    System.out.println("Syns size of " + userAnswer.get(j)[0] + " " + userAnswer.get(j)[1] + ": " + synonyms.size());
                    while (k < synonyms.size() && !resetCycle) {
                        // Compare i with k, because i and j were compared already
                        System.out.println(realAnswer.get(i)[0] + "-" + realAnswer.get(i)[1] + " " + synonyms.get(k)[0] + "-" + synonyms.get(k)[1]);
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
                    if (!resetCycle && !synonyms.isEmpty()) {
                        ns.addNearestSynonymString(nearestString);
                        ns.addNearestSynonymPercentage(nearestStringValue);
                    }
                    j++;
                }
                i++;
            }
            
            // POS tag constant
            double posTagConstant = 1 - (similarityOutput.getBasePOSTagCountSame() / similarityOutput.getBasePOSTagSize());

            similarity.setRealAnswer(realAnswer);
            similarity.setUserAnswer(userAnswer);
            similarity.setSimilarityPercentage(similarity.getSimilarityPercentage() + ((double) countSame / (double) defaultComparatorSize) * posTagConstant);
            similarity.setNearestSynonym(ns);
        }
        
        System.out.println("***");
        return similarity;
    }
    
    /**
     *
     * @param similarityOutput
     * @return
     */
    public SimilarityOutput getThirdSimilarity (SimilarityOutput similarityOutput) {
        for(int i = 0; i < similarityOutput.getNearestSynonym().getNearestSynonymString().size(); i++) {
            System.out.println("lel: " + similarityOutput.getNearestSynonym().getNearestSynonymString()
                    .get(i)[0] + " " + similarityOutput.getNearestSynonym().getNearestSynonymString()
                    .get(i)[1] + " " + similarityOutput.getNearestSynonym().
                    getNearestSynonymPercentage().get(i));
        }
        
        // Jaro Winkler
        SimilarityOutput similarity = new SimilarityOutput(similarityOutput);
        ArrayList<String[]> userAnswer = similarityOutput.getUserAnswer(), 
                            realAnswer = similarityOutput.getRealAnswer();
        JaroWinkler jaroWinkler = new JaroWinkler();
        int defaultComparatorSize = similarityOutput.getBasePOSTagSize();
        
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
        
        // POS tag constant
        double posTagConstant = 1 - (similarityOutput.getBasePOSTagCountSame() / similarityOutput.getBasePOSTagSize());
        System.out.println("POStagCS: " + similarityOutput.getBasePOSTagCountSame());
        System.out.println("POStagBS: " + similarityOutput.getBasePOSTagSize());
        
        // Excessive
        double excessivePenalty = 0d;
        if (similarityOutput.getExcessivePOSWord() == 0) {
            // if POS word excessive == 0 there might be 2nd and 3rd similarity was skipped
            if (similarityOutput.getExcessivePOSWord() != 0) {
                // penalty
                excessivePenalty = (double) similarityOutput.getExcessivePOSWord() / 
                        (double) (similarityOutput.getBasePOSTagSize() + similarityOutput.getExcessivePOSWord());
                System.out.println("penaltya: " + excessivePenalty + " " + similarityOutput.getExcessivePOSWord());
            }
        } else {
            // there is excessive word
            excessivePenalty = (double) similarityOutput.getExcessivePOSWord()/ 
                        (double) (similarityOutput.getBasePOSTagSize() + similarityOutput.getBasePOSTagSize());
                System.out.println("penaltyb: " + excessivePenalty + " " + similarityOutput.getExcessivePOSWord());
        }
        
        // Jaro-Winkler
        double jw = jaroWinkler.apply(concatSentence, concatSentenceComparison);
        
        // Calculate overall
        double similarityOverall = similarity.getSimilarityPercentage() + (jw / (double)defaultComparatorSize * posTagConstant);
        similarityOverall -= excessivePenalty;
        
        similarity.setSimilarityPercentage(similarityOverall);
        
        return similarity;
    }
    
    /**
     *
     * @param realAnswer
     * @param userAnswer
     * @throws SQLException
     */
    public void compareSentence(String realAnswer, String userAnswer) throws SQLException {
        if (countWordsBySpaces(realAnswer) > 1 && countWordsBySpaces(userAnswer) > 1) {
            // more than one
            SimilarityOutput sim1, sim2, sim3;
            
            String synthesizedRealAnswer = processFormalizeSentence(realAnswer);
            String synthesizedUserAnswer = processFormalizeSentence(userAnswer);
            
            // POS tagging
            sim1 = getFirstSimilarity(synthesizedRealAnswer, synthesizedUserAnswer);
            // Synonym
            sim2 = getSecondSimilarity(sim1);
            // Jaro Winkler
            sim3 = getThirdSimilarity(sim2);
            System.out.println(sim3.getSimilarityPercentage());
        } else {
            JaroWinkler jw = new JaroWinkler();
            System.out.println(jw.apply(realAnswer, userAnswer));
        }
        System.out.println("*** END OF GRADING PROCESS ***");
    }
    
    /**
     *
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {
        StringProcessor sp = new StringProcessor();
        String s = "Pada hakikatnya, seorang bayi yang baru lahir tergolong suci.";
        String t = "Pada hakikatnya, seorang bayi yang baru lahir tergolong bersih.";
        
//        ArrayList<String[]> str = IndonesianPOSTagger.doPOSTag(t);
//        for(String[] xs : str) {
//            System.out.println(xs[0] + " " + xs[1]);
//        }
        
        sp.compareSentence(s, t);
    }
}