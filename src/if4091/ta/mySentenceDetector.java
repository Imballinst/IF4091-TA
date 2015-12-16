/*
 * This base of this source code was originally taken from Apache's OpenNLP Documentation.
 * Development by Imballinst (imballinst.github.io)
 */
package if4091.ta;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.sentdetect.SentenceDetectorFactory;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 *
 * @author Imballinst
 */
public class mySentenceDetector {
    /**
     *
     * @param trainingDataDirectory
     * @param modelDataDirectory
     * @param charset
     * @throws IOException
     */
    public void trainSentenceDetector(String trainingDataDirectory, String modelDataDirectory, Charset charset) throws IOException {
        // Local variables for model
        File f = new File(trainingDataDirectory + "id-sent.train");
        ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(f), charset);
        ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);
        
        SentenceModel model;
        char[] eos = {';', '.', '!', '?' };
        Dictionary dic = new Dictionary();
        OutputStream modelOut = null;
        
        // Train the model using the training data
        try {
            model = SentenceDetectorME.train("en", sampleStream, new SentenceDetectorFactory("en", true, dic, eos), TrainingParameters.defaultParams());
        }
        finally {
            sampleStream.close();
        }
        
        try {
            // Copy files
            copyFiles(trainingDataDirectory, modelDataDirectory);
            
            // Create the model file
            modelOut = new BufferedOutputStream(new FileOutputStream(modelDataDirectory + "\\sentenceDetector\\id-sent.bin"));
            model.serialize(modelOut);
        } finally {
            if (modelOut != null) {
                modelOut.close();
            }
        }
    }
    
    public void copyFiles(String srcPath, String destPath) throws FileNotFoundException, IOException {
        // Local variables for copying training data to evaluation data
        FileChannel src = new FileInputStream(srcPath + "id-sent.train").getChannel();
        FileChannel dest = new FileOutputStream(destPath + "\\sentenceDetector\\id-sent.eval").getChannel();
        FileChannel dest2 = new FileOutputStream(destPath + "\\sentenceDetector\\test.txt").getChannel();
        
        try {
            // Copy the content from src to dest
            dest.transferFrom(src, 0, src.size());
            src.position(0);
            dest2.transferFrom(src, 0, src.size());
        } finally {
            src.close();
            dest.close();
        }
        
        // replace test.txt newlines with whitespaces
        
        Path path = Paths.get(destPath + "\\sentenceDetector\\test.txt");
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(path), charset);
        content = content.replaceAll("\n", " ");
        Files.write(path, content.getBytes(charset));
    }
}
