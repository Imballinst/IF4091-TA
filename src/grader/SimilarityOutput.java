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

import java.util.ArrayList;

/**
 *
 * @author Try
 */
public class SimilarityOutput {
    private Double similarityPercentage;
    private int basePOSTagSize;
    private int basePOSTagCountSame;
    private int excessivePOSWord;
    private ArrayList<String[]> realAnswer;
    private ArrayList<String[]> userAnswer;
    private NearestSynonym nearestSynonym;
    
    /**
     * Constructor
     */
    public SimilarityOutput() {
        similarityPercentage = 0d;
        basePOSTagSize = 0;
        basePOSTagCountSame = 0;
        excessivePOSWord = 0;
        realAnswer = new ArrayList<>();
        userAnswer = new ArrayList<>();
        nearestSynonym = new NearestSynonym();
    }
    
    /**
     *
     * @param s
     */
    public SimilarityOutput(SimilarityOutput s) {
        this.similarityPercentage = s.similarityPercentage;
        this.basePOSTagSize = s.basePOSTagSize;
        this.basePOSTagCountSame = s.basePOSTagCountSame;
        this.excessivePOSWord = s.excessivePOSWord;
        this.realAnswer = s.realAnswer;
        this.userAnswer = s.userAnswer;
        this.nearestSynonym = s.nearestSynonym;
    }

    /**
     * @return the similarityPercentage
     */
    public Double getSimilarityPercentage() {
        return similarityPercentage;
    }

    /**
     * @param similarityPercentage the similarityPercentage to set
     */
    public void setSimilarityPercentage(Double similarityPercentage) {
        this.similarityPercentage = similarityPercentage;
    }

    /**
     * @return the realAnswer
     */
    public ArrayList<String[]> getRealAnswer() {
        return realAnswer;
    }
    
    /**
     *
     * @return
     */
    public ArrayList<String[]> getRealAnswerClone() {
        ArrayList<String[]> arr = new ArrayList<>();
        for (int i = 0; i < realAnswer.size(); i++) {
            arr.add(realAnswer.get(i).clone());
        }
        
        return arr;
    }

    /**
     * @param realAnswer the realAnswer to set
     */
    public void setRealAnswer(ArrayList<String[]> realAnswer) {
        this.realAnswer = realAnswer;
    }

    /**
     * @return the userAnswer
     */
    public ArrayList<String[]> getUserAnswer() {
        return userAnswer;
    }
    
    /**
     *
     * @return
     */
    public ArrayList<String[]> getUserAnswerClone() {
        ArrayList<String[]> arr = new ArrayList<>();
        for (int i = 0; i < userAnswer.size(); i++) {
            arr.add(userAnswer.get(i).clone());
        }
        
        return arr;
    }

    /**
     * @param userAnswer the userAnswer to set
     */
    public void setUserAnswer(ArrayList<String[]> userAnswer) {
        this.userAnswer = userAnswer;
    }

    /**
     * @return the nearestSynonym
     */
    public NearestSynonym getNearestSynonym() {
        return nearestSynonym;
    }

    /**
     * @param nearestSynonym the nearestSynonym to set
     */
    public void setNearestSynonym(NearestSynonym nearestSynonym) {
        this.nearestSynonym = nearestSynonym;
    }

    /**
     * @return the basePOSTagSize
     */
    public int getBasePOSTagSize() {
        return basePOSTagSize;
    }

    /**
     * @param basePOSTagSize the basePOSTagSize to set
     */
    public void setBasePOSTagSize(int basePOSTagSize) {
        this.basePOSTagSize = basePOSTagSize;
    }

    /**
     * @return the excessivePOSWord
     */
    public int getExcessivePOSWord() {
        return excessivePOSWord;
    }

    /**
     * @param excessivePOSWord the excessivePOSWord to set
     */
    public void setExcessivePOSWord(int excessivePOSWord) {
        this.excessivePOSWord = excessivePOSWord;
    }

    /**
     * @return the basePOSTagCountSame
     */
    public int getBasePOSTagCountSame() {
        return basePOSTagCountSame;
    }

    /**
     * @param basePOSTagCountSame the basePOSTagCountSame to set
     */
    public void setBasePOSTagCountSame(int basePOSTagCountSame) {
        this.basePOSTagCountSame = basePOSTagCountSame;
    }
}
