/*
 * This base of this source code was originally taken from Apache's OpenNLP Documentation.
 * Development by Imballinst (imballinst.github.io)
 */
package if4091.ta;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;

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
        // Instantiate class
//        MySentenceDetector sentenceDetector = new MySentenceDetector();
        MyCategorizer categorizer = new MyCategorizer();
        
        // Variables
        String projectDirectory = System.getProperty("user.dir");
        String trainingDataDirectory = projectDirectory + "\\src\\if4091\\training-data\\";
        String modelDataDirectory = projectDirectory + "\\model\\";
        Charset charset = Charset.forName("UTF-8");				
        
        // Train
//        sentenceDetector.trainSentenceDetector(trainingDataDirectory, modelDataDirectory, charset);
        categorizer.trainDocumentCategorizer(trainingDataDirectory, modelDataDirectory, charset);
        String input = "";
        Scanner in = new Scanner(System.in);
        do {
            System.out.print("Enter your desired word: ");
            input = in.nextLine();
            categorizer.detectCategory(trainingDataDirectory, modelDataDirectory, input);
        } while (input.compareTo("q") != 0);
    }
}
