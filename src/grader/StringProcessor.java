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
import java.util.ArrayList;

/**
 *
 * @author Try
 */
public class StringProcessor {
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
    
    public ArrayList<String[]> removeUnimportantWords(ArrayList<String[]> arr) {
        ArrayList<String[]> filteredWords = new ArrayList<>();
        
        // Remove UH, IN, and CC POS tag
        for(int i = 0; i < arr.size(); i++) {
            if (arr.get(i)[1].compareTo("UH") != 0 &&
                arr.get(i)[1].compareTo("IN") != 0 &&
                arr.get(i)[1].compareTo("CC") != 0) {
                // Add to new ArrayList
                filteredWords.add(arr.get(i));
            }
        }
        
        return filteredWords;
    }
    
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
        StringProcessor sp = new StringProcessor();
        String s = "Saya senang bermain bola dan kotak";
        ArrayList<String[]> s2 = sp.generatePOSTag(s);
        ArrayList<String[]> s3 = sp.removeUnimportantWords(s2);
        for(String[] x : s3) {
            System.out.println(x[0] + " " + x[1]);
        }
    }
}
