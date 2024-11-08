import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;

public class inspectfm {

    public static void main(String[] args) {
        String indexPath = args[0];
        int sampleRate = Integer.parseInt(args[1]);
        String outputPath = args[2];

        int[] sa;
        String bwt;
        int[] firstColumn;  // First column should now be an int[] with 5 elements
        int[][] tally;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(indexPath))) {
            sa = (int[]) in.readObject();
            bwt = (String) in.readObject();
            firstColumn = (int[]) in.readObject();  // Deserialize as int[]
            tally = (int[][]) in.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (PrintWriter writer = new PrintWriter(outputPath)) {
            // Print first column counts: $, A, C, G, T
            writer.println(String.join("\t", 
                    String.valueOf(firstColumn[0]),  // Count of '$'
                    String.valueOf(firstColumn[1]),  // Count of 'A'
                    String.valueOf(firstColumn[2]),  // Count of 'C'
                    String.valueOf(firstColumn[3]),  // Count of 'G'
                    String.valueOf(firstColumn[4]))); // Count of 'T'

            // Print BWT
            writer.println(bwt);

            // Print tally spot check for A, C, G, T
            for (int i = 0; i < 4; i++) {
                StringBuilder line = new StringBuilder("tally spot check ");
                char c = "ACGT".charAt(i);
                line.append(c).append("\t");
                for (int j = 0; j < tally[i].length; j++) {
                    if (j % sampleRate == 0) {  // Only print tally every sampleRate-th index
                        line.append(tally[i][j]).append("\t");
                    }
                }
                writer.println(line.toString().trim());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
