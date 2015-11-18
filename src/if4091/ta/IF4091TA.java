/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package if4091.ta;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.sentdetect.SentenceDetectorFactory;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 *
 * @author Imballinst
 */
public class IF4091TA {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        String dir = "C:\\Users\\Imballinst\\Documents\\NetBeansProjects\\IF4091-TA\\src\\if4091\\ta\\";
        Charset charset = Charset.forName("UTF-8");				
        ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(dir + "en-sent.train"), charset);
        ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);

        SentenceModel model;
        
        char[] eos = {';', '.', '!', '?' };
        Dictionary dic = new Dictionary();
        
        try {
          model = SentenceDetectorME.train("en", sampleStream, new SentenceDetectorFactory("en", true, dic, eos), TrainingParameters.defaultParams());
                  //SentenceDetectorME.train("en", sampleStream, true, null, TrainingParameters.defaultParams());
        }
        finally {
          sampleStream.close();
        }

        OutputStream modelOut = null;
        try {
          modelOut = new BufferedOutputStream(new FileOutputStream(dir + "en-sent.bin"));
          model.serialize(modelOut);
        } finally {
          if (modelOut != null) 
             modelOut.close();      
        }
    }
}
