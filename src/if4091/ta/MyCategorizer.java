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
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 *
 * @author Imballinst
 */
public class MyCategorizer {
    /**
     *
     * @param trainingDataDirectory
     * @param modelDataDirectory
     * @param charset
     * @throws IOException
     */
    public void trainDocumentCategorizer(String trainingDataDirectory, String modelDataDirectory, Charset charset) throws IOException {
        // Local variables for model
        File f = new File(trainingDataDirectory + "id-cat.train");
        ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(f), charset);
        ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);
        
        DoccatModel model = null;
        OutputStream modelOut = null;
        
        // Train the model using the training data
        try {
            model = DocumentCategorizerME.train("id", sampleStream, TrainingParameters.defaultParams(), new DoccatFactory());
        }
        finally {
            sampleStream.close();
        }
        
        try {
            // Copy files
            copyFiles(trainingDataDirectory, modelDataDirectory);
            
            // Create the model file
            modelOut = new BufferedOutputStream(new FileOutputStream(modelDataDirectory + "\\categorizer\\id-cat.bin"));
            model.serialize(modelOut);
        } finally {
            if (modelOut != null) {
                modelOut.close();
            }
        }
    }
    
    public void copyFiles(String srcPath, String destPath) throws FileNotFoundException, IOException {
        // Local variables for copying training data to evaluation data
        FileChannel src = new FileInputStream(srcPath + "id-cat.train").getChannel();
        FileChannel dest2 = new FileOutputStream(destPath + "\\categorizer\\test.txt").getChannel();
        
        try {
            // Copy the content from src to dest
            dest2.transferFrom(src, 0, src.size());
        } finally {
            src.close();
            dest2.close();
        }
        
        // replace test.txt newlines with whitespaces
        
        Path path = Paths.get(destPath + "\\categorizer\\test.txt");
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(path), charset), newcontent = "";
        
        String[] x = content.split("\n");
        for(String y : x) {
            String[] z = y.split(" ", 2);
            newcontent += z[1] + "\n";
        }
        
        Files.write(path, newcontent.getBytes(charset));
    }
    
    public void detectCategory(String trainingDataDirectory, String modelDataDirectory, String word) throws IOException {
        FileInputStream inputStream = new FileInputStream(modelDataDirectory + "\\categorizer\\id-cat.bin");
        DoccatModel docCatModel = new DoccatModel(inputStream);
        DocumentCategorizerME myCategorizer = new DocumentCategorizerME(docCatModel);
        
        double[] outcomes = myCategorizer.categorize(word);
        String category = myCategorizer.getBestCategory(outcomes);
        System.out.println(category);
    }
}
