/*
 * This base of this source code was originally taken from Apache's OpenNLP Documentation.
 * Development by Imballinst (imballinst.github.io)
 */
package if4091.ta;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
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
    public static void main(String[] args) throws IOException {
        // Variables
        String projectDirectory = System.getProperty("user.dir");
        String trainingDataDirectory = projectDirectory + "\\src\\if4091\\training-data\\";
        String modelDataDirectory = projectDirectory + "\\src\\if4091\\model\\";
        Charset charset = Charset.forName("UTF-8");				
        
        // Train
        trainSentenceDetector(trainingDataDirectory, modelDataDirectory, charset);
    }
    
    /**
     *
     * @param sourceDirectory
     * @param modelDirectory
     * @param charset
     * @throws IOException
     */
    public static void trainSentenceDetector(String trainingDataDirectory, String modelDataDirectory, Charset charset) throws IOException {
        // Model exact directory
        String sentenceDetectorModelDirectory = modelDataDirectory + "\\sentenceDetector\\";
        
        // Local variables for model
        ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(trainingDataDirectory + "en-sent.train"), charset);
        ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);
        SentenceModel model;
        char[] eos = {';', '.', '!', '?' };
        Dictionary dic = new Dictionary();
        OutputStream modelOut = null;
        
        // Local variables for copying training data to evaluation data
        FileChannel src = new FileInputStream(trainingDataDirectory + "en-sent.train").getChannel();
        FileChannel dest = new FileOutputStream(sentenceDetectorModelDirectory + "en-sent.eval").getChannel();
        
        // Train the model using the training data
        try {
            model = SentenceDetectorME.train("en", sampleStream, new SentenceDetectorFactory("en", true, dic, eos), TrainingParameters.defaultParams());
        }
        finally {
            sampleStream.close();
        }
        
        try {
            // Copy the content from src to dest
            dest.transferFrom(src, 0, src.size());
            
            // Create the model file
            modelOut = new BufferedOutputStream(new FileOutputStream(sentenceDetectorModelDirectory + "en-sent.bin"));
            model.serialize(modelOut);
        } finally {
            if (modelOut != null) {
                modelOut.close();
                src.close();
                dest.close();
            }
        }
    }
}
