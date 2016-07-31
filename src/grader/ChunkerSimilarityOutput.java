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
public class ChunkerSimilarityOutput {

    /**
     *
     */
    private Double similarityPercentage;

    /**
     *
     */
    private int baseChunkerSize;

    /**
     *
     */
    private ArrayList<String[]> realAnswer;

    /**
     *
     */
    private ArrayList<String[]> userAnswer;
    
    /**
     *
     */
    private NearestSynonym nearestSynonym;
    
    /**
     *
     */
    private int basePOSTagSize;
    
    /**
     *
     */
    public ChunkerSimilarityOutput() {
        similarityPercentage = 0d;
        baseChunkerSize = 0;
        realAnswer = new ArrayList<>();
        userAnswer = new ArrayList<>();
        nearestSynonym = new NearestSynonym();
    }
    
    /**
     *
     * @param s
     */
    public ChunkerSimilarityOutput(ChunkerSimilarityOutput s) {
        this.similarityPercentage = s.similarityPercentage;
        this.baseChunkerSize = s.baseChunkerSize;
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
     * @return the baseSentenceSize
     */
    public int getBaseChunkerSize() {
        return baseChunkerSize;
    }

    /**
     * @param baseChunkerSize the baseSentenceSize to set
     */
    public void setBaseChunkerSize(int baseChunkerSize) {
        this.baseChunkerSize = baseChunkerSize;
    }

    /**
     * @return the realAnswer
     */
    public ArrayList<String[]> getRealAnswer() {
        return realAnswer;
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
}
