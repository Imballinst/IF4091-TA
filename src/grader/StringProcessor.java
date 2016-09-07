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
    public ArrayList<String[]> processStem(ArrayList<String[]> word) {
        IndonesianStemmer stemmer = new IndonesianStemmer();
        ArrayList<String[]> str = new ArrayList();
        
        for(String[] eachWord : word) {
            String stemmedWord[] = new String[2];
            stemmedWord[0] = stemmer.stem(eachWord[0]);
            stemmedWord[1] = eachWord[1];
            str.add(stemmedWord);
        }
        
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
    
    
    public static String difference(String str1, String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        int at = indexOfDifference(str1, str2);
        if (at == -1) {
            return null;
        }
        return str2.substring(at);
    }
    
    public static int indexOfDifference(String str1, String str2) {
        if (str1 == str2) {
            return -1;
        }
        if (str1 == null || str2 == null) {
            return 0;
        }
        int i;
        for (i = 0; i < str1.length() && i < str2.length(); ++i) {
            if (str1.charAt(i) != str2.charAt(i)) {
                break;
            }
        }
        if (i < str2.length() || i < str1.length()) {
            return i;
        }
        return -1;
    }
    
    /**
     *
     * @param str1
     * @param str2
     * @return
     */
    public boolean isSame(String[] str1, String[] str2) {
        return (str1[0].trim().toLowerCase().compareTo(str2[0].trim().toLowerCase()) == 0 && 
                isPOSTagSame(str1[1], str2[1]));
    }
    
    public boolean isSameWithNearestSynonymString(String[] realAnswer, String[] synonym) {
        return (realAnswer[0].toLowerCase().compareTo(synonym[4].toLowerCase()) == 0 && 
                isPOSTagSame(realAnswer[1], synonym[5]));
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
        
        // Handle single stopword
        if (posTag1.length() == 1 && posTag2.length() == 1) {
            isSame = (posTag1.compareTo(posTag2) == 0);
        } else {
            while (isSame && charIdx < maxIdx) {
                if(posTag1.charAt(charIdx) != posTag2.charAt(charIdx)) {
                    isSame = false;
                }
                charIdx++;
            }
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
        ArrayList<String[]> realAnswer = processPOSTag(_realAnswer),
                            userAnswer = processPOSTag(_userAnswer);
        
        realAnswer = removeUnimportantWords(realAnswer);
        userAnswer = removeUnimportantWords(userAnswer);
        
        // POS tag comparison
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
        
        similarity.setRealAnswer(realAnswer);
        similarity.setUserAnswer(userAnswer);
        similarity.setBasePOSTagCountSame(countSame);
        similarity.setBasePOSTagSize(defaultComparatorSize);
        similarity.setSimilarityPercentage(similarity.getSimilarityPercentage() + ((double) countSame / (double) defaultComparatorSize));
        
        System.out.println("Excessive: " + similarity.getExcessivePOSWord());
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
        
        if (!userAnswer.isEmpty() && !realAnswer.isEmpty()) {
            int i = 0, j, k; //index
            int countSame = 0;
            
            while (i < realAnswer.size()) {
                boolean resetCycle = false;
                j = 0;
                while (j < userAnswer.size() && !resetCycle) {
                    // Get synonyms from word and pos
                    k = 0;
                    nearestStringValue = 0d;
                    nearestString = new String[6];
                    synonyms = dbm.getSynonyms(realAnswer.get(i)[0], realAnswer.get(i)[1]);
                    while (i < userAnswer.size() && k < synonyms.size() && !resetCycle) {
                        // Compare i with k, because i and j were compared already
                        if (isSame(userAnswer.get(i), synonyms.get(k))) {
                            System.out.println("Found the same synonym");
                            realAnswer.remove(i);
                            userAnswer.remove(j);
                            i--;
                            j--;
                            resetCycle = true;
                            countSame++;
                        } else {
                            Double temp = jw.apply(userAnswer.get(i)[0], 
                                                   synonyms.get(k)[0]);

                            if (temp > nearestStringValue) {
                                // Real answer string and pos tag
                                nearestString[0] = realAnswer.get(i)[0];
                                nearestString[1] = realAnswer.get(i)[1];
                                // User answer string and pos tag
                                nearestString[2] = userAnswer.get(j)[0];
                                nearestString[3] = userAnswer.get(j)[1];
                                // Synonyms string and pos tag
                                nearestString[4] = synonyms.get(k)[0];
                                nearestString[5] = synonyms.get(k)[1];
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
            double posTagConstant = 1 - ((double) similarityOutput.getBasePOSTagCountSame() / (double) similarityOutput.getBasePOSTagSize());
            
            similarity.setRealAnswer(realAnswer);
            similarity.setUserAnswer(userAnswer);
            similarity.setSimilarityPercentage(similarity.getSimilarityPercentage() + (double) countSame * posTagConstant);
            similarity.setNearestSynonym(ns);
        }
        
        System.out.println("Excessive: " + similarity.getExcessivePOSWord());
        System.out.println("***");
        return similarity;
    }
    
    /**
     *
     * @param similarityOutput
     * @return
     */
    public SimilarityOutput getThirdSimilarity (SimilarityOutput similarityOutput) {
//        for(int i = 0; i < similarityOutput.getNearestSynonym().getNearestSynonymString().size(); i++) {
//            System.out.println("lel: " + similarityOutput.getNearestSynonym().getNearestSynonymStringRealAnswer(i) + "-" + 
//                    similarityOutput.getNearestSynonym().getNearestSynonymStringRealAnswerPOSTag(i) + " " + 
//                    similarityOutput.getNearestSynonym().getNearestSynonymStringUserAnswer(i) + "-" +
//                    similarityOutput.getNearestSynonym().getNearestSynonymStringUserAnswerPOSTag(i) + " " +
//                    similarityOutput.getNearestSynonym().getNearestSynonymStringSynonymString(i) + "-" +
//                    similarityOutput.getNearestSynonym().getNearestSynonymStringSynonymStringPOSTag(i) + " " +
//                    similarityOutput.getNearestSynonym().getNearestSynonymPercentage().get(i));
//        }
        
        // Jaro Winkler
        SimilarityOutput similarity = new SimilarityOutput(similarityOutput);
        ArrayList<String[]> userAnswer = similarityOutput.getUserAnswer(),
                            userAnswerRepl = similarityOutput.getUserAnswerClone(),
                            realAnswerRepl = similarityOutput.getRealAnswerClone(),
                            realAnswer = similarityOutput.getRealAnswer();
        JaroWinkler jaroWinkler = new JaroWinkler();
        String realSentence = "", userSentence = "", realSentenceRepl = "", userSentenceRepl = "";
        
        // POS tag constant
        double posTagConstant = 1 - ((double) similarityOutput.getBasePOSTagCountSame() / (double) similarityOutput.getBasePOSTagSize());
        
        // Real
        for (int i = 0; i < realAnswer.size(); i++) {
            realSentence += realAnswer.get(i)[0];
            if (i + 1 < realAnswer.size()) {
                // Add whitespace
                realSentence += " ";
            }
        }
        for (int i = 0; i < userAnswer.size(); i++) {
            userSentence += userAnswer.get(i)[0];
            if (i + 1 < userAnswer.size()) {
                // Add whitespace
                userSentence += " ";
            }
        }
        
        
        
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
        double jw = jaroWinkler.apply(realSentence, userSentence);
        
        // If there is synonym, try replace
        NearestSynonym tempNS = new NearestSynonym();
        double tempDouble = 0d, jw2 = 0d;
        
        if (!tempNS.getNearestSynonymString().isEmpty()) {
            // Replace synonym word with better one
            tempNS = similarityOutput.getNearestSynonym();
            tempDouble = 0d;

            for (int i = 0; i < userAnswerRepl.size(); i++) {
                // Count jaro-winkler for userAnswer word vs nearestSynonymString, find pairs who have accuracy above 0.85
                double tmp = jaroWinkler.apply(userAnswerRepl.get(i)[0], tempNS.getNearestSynonymStringSynonymString(i));

                if(tmp > jaroWinkler.apply(userAnswerRepl.get(i)[0], realAnswer.get(i)[0]) && tmp > 0.85) {
                    System.out.println(userAnswerRepl.get(i)[0] + " " + tempNS.getNearestSynonymStringSynonymString(i) + " " + realAnswer.get(i)[0] + " " + tmp);
                    tempDouble += (tmp * posTagConstant);
                    userAnswerRepl.remove(i);
                    realAnswerRepl.remove(i);
                    i--;
                }
            }
            
            // With Synonym and value replacement
            for (int i = 0; i < realAnswerRepl.size(); i++) {
                realSentenceRepl += realAnswerRepl.get(i)[0];
                if (i + 1 < realAnswerRepl.size()) {
                    // Add whitespace
                    realSentenceRepl += " ";
                }
            }
            for (int i = 0; i < userAnswerRepl.size(); i++) {
                userSentenceRepl += userAnswerRepl.get(i)[0];
                if (i + 1 < userAnswerRepl.size()) {
                    // Add whitespace
                    realSentenceRepl += " ";
                }
            }
            
            jw2 = jaroWinkler.apply(realSentenceRepl, userSentenceRepl);
        }
        
        // Calculate overall
        double similarityOverallReal = similarity.getSimilarityPercentage() + (jw * posTagConstant),
               similarityOverallRepl = similarity.getSimilarityPercentage() + tempDouble + (jw2 * posTagConstant);
        double similarityOverall = similarityOverallReal > similarityOverallRepl ? similarityOverallReal : similarityOverallRepl;
        
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
        if (processSplitSentence(realAnswer).size() == 1 && processSplitSentence(userAnswer).size() == 1) {
            // single sentence
            System.out.println(handleSingleSentence(realAnswer, userAnswer));
        } else if (processSplitSentence(realAnswer).size() == 0 && processSplitSentence(userAnswer).size() == 0) {
            System.out.println(1);
        } else if ((processSplitSentence(realAnswer).size() == 1 && processSplitSentence(userAnswer).size() == 0) ||
                   (processSplitSentence(realAnswer).size() == 0 && processSplitSentence(userAnswer).size() == 1)) {
            System.out.println(0);
        } else {
            // multiple sentences
            System.out.println(handleMultipleSentence(realAnswer, userAnswer));
        }
        System.out.println("*** END OF GRADING PROCESS ***");
    }
    
    public double handleSingleSentence(String _realAnswer, String _userAnswer) throws SQLException {
        if (countWordsBySpaces(_realAnswer) > 2 && countWordsBySpaces(_userAnswer) > 2) {
            // more than one word
            SimilarityOutput sim1, sim2, sim3;
        
            String synthesizedRealAnswer = processFormalizeSentence(_realAnswer);
            String synthesizedUserAnswer = processFormalizeSentence(_userAnswer);
            // POS tagging
            sim1 = getFirstSimilarity(synthesizedRealAnswer, synthesizedUserAnswer);
            // Synonym
            sim2 = getSecondSimilarity(sim1);
            // Jaro Winkler
            sim3 = getThirdSimilarity(sim2);

            return sim3.getSimilarityPercentage();
        } else {
            JaroWinkler jw = new JaroWinkler();
            return jw.apply(_realAnswer, _userAnswer);
        }
    }
    
    public double handleMultipleSentence(String _realAnswer, String _userAnswer) throws SQLException {
        
        // Matrix
        ArrayList<ArrayList<Double>> arrStrValue = new ArrayList<>();
        ArrayList<String> realAns = new ArrayList<>(), userAns = new ArrayList<>();
        
        realAns = processSplitSentence(_realAnswer);
        userAns = processSplitSentence(_userAnswer);
        
        double avgScore = 0d;
        int pairs = 0;
        
        // Fill matrices
        for (int i = 0; i < realAns.size(); i++) {
            ArrayList<Double> tmp = new ArrayList<>();
            for (int j = 0; j < userAns.size(); j++) {
                double val = handleSingleSentence(realAns.get(i), userAns.get(j));
                tmp.add(val);
            }
            arrStrValue.add(tmp);
        }
        
        System.out.println("*** MATRIX ***");
        for (int i = 0; i < arrStrValue.size(); i++) {
            for (int j = 0; j < arrStrValue.get(i).size(); j++) {
                System.out.print(arrStrValue.get(i).get(j) + " ");
            }
            System.out.println("");
        }
        System.out.println("*** END MATRIX ***");
        
        // Find index max
        int rowLimit = arrStrValue.size();
        
        for (int i = 0; i < rowLimit; i++) {
            int colLimit = arrStrValue.get(i).size();
            boolean removedUserAns = false;
            // real ans array
            double maxRow = 0d;
            int maxId = 0;
            for (int j = 0; j < arrStrValue.get(i).size(); j++) {
                // user ans array
                if (arrStrValue.get(i).get(j) > maxRow) {
                    maxRow = arrStrValue.get(i).get(j);
                    maxId = j;
                }
            }
            
            // the maxId is fixed already, remove maxId from the rest of the table
            for (int k = i; k < rowLimit; k++) {
                boolean found = false;
                int l = 0;
                while (!found && l < colLimit) {
                    if (l == maxId) {
                        if(!removedUserAns) {
                            System.out.println("Removed user answer " + userAns.get(l) + " of value " + arrStrValue.get(k).get(l));
                            avgScore += arrStrValue.get(k).get(l);
                            pairs++;
                            userAns.remove(l);
                            removedUserAns = true;
                        }
                        arrStrValue.get(k).remove(l);
                        found = true;
                    }
                    l++;
                }
            }
        }
        
        System.out.println(avgScore + " " + pairs);
        System.out.println("***");
        // Remove column with max index
        return (avgScore / pairs);
    }
}