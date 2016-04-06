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
        String txt1 = "Setengah dari populasi.";
        String txt2 = "Dua puluh lima ribu orang dewasa.";
        double d = LetterPairSimilarity.compareStrings(txt1, txt2);
        System.out.println(d);
    }
}
