/*
 * Copyright 2016 imballinst.
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
 * @author imballinst
 */
public class NearestSynonym {
    private ArrayList<String[]> nearestSynonymString;
    private ArrayList<Double> nearestSynonymPercentage;
    
    /**
     * Constructor
     */
    public NearestSynonym() {
        this.nearestSynonymPercentage = new ArrayList<>();
        this.nearestSynonymString = new ArrayList<>();
    }
    
    /**
     *
     * @param nearestSynonymString_
     * @param nearestSynonymPercentage_
     */
    public NearestSynonym(ArrayList<String[]> nearestSynonymString_, 
                          ArrayList<Double> nearestSynonymPercentage_) {
        this.nearestSynonymString = nearestSynonymString_;
        this.nearestSynonymPercentage = nearestSynonymPercentage_;
    }
    
    /**
     *
     * @return
     */
    public ArrayList<String[]> getNearestSynonymString() {
        return nearestSynonymString;
    }
    
    /**
     *
     * @return
     */
    public ArrayList<Double> getNearestSynonymPercentage() {
        return nearestSynonymPercentage;
    }
    
    /**
     *
     * @param str
     */
    public void addNearestSynonymString(String[] str) {
        nearestSynonymString.add(str);
    }
    
    /**
     *
     * @param d
     */
    public void addNearestSynonymPercentage(Double d) {
        nearestSynonymPercentage.add(d);
    }
    
    public String getNearestSynonymStringRealAnswer(int idx) {
        return nearestSynonymString.get(idx)[0];
    }
    
    public String getNearestSynonymStringRealAnswerPOSTag(int idx) {
        return nearestSynonymString.get(idx)[1];
    }
    
    public String getNearestSynonymStringUserAnswer(int idx) {
        return nearestSynonymString.get(idx)[2];
    }
    
    public String getNearestSynonymStringUserAnswerPOSTag(int idx) {
        return nearestSynonymString.get(idx)[3];
    }
    
    public String getNearestSynonymStringSynonymString(int idx) {
        return nearestSynonymString.get(idx)[4];
    }
    
    public String getNearestSynonymStringSynonymStringPOSTag(int idx) {
        return nearestSynonymString.get(idx)[5];
    }
    
    public int size() {
        return nearestSynonymString.size();
    }
}
