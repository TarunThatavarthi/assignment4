import java.io.*;
import java.nio.file.*;
import java.util.*;

public class buildfm {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("error");
            return;
        }

        String reference = readFasta(args[0]) + "$";  // Append '$' to the reference string
        int[] suffixArray = buildSuffixArray(reference);
        String bwt = buildBurrowsWheeler(reference, suffixArray);
        int[] firstColumn = buildFirstColumn(reference);  // Build first column (including '$')
        int[][] tally = buildTally(bwt);  // Tally counts of A, C, G, T in BWT
        String outfile = args[1];

        try {
            serialize(suffixArray, bwt, firstColumn, tally, outfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reads genome from FASTA file, ignoring headers
    public static String readFasta(String fastaFile) {
        System.out.println("Reading FASTA file from: " + fastaFile);

        StringBuilder sb = new StringBuilder();
        try {
            Files.readAllLines(Paths.get(fastaFile)).forEach(line -> {
                if (!line.startsWith(">")) sb.append(line.trim());
            });
        } catch (IOException e) {
            System.out.println(e);
        }
        return sb.toString();
    }

    // Builds suffix array by sorting suffix indices
    public static int[] buildSuffixArray(String ref) {
        int len = ref.length();
        Integer[] suffixes = new Integer[len];
        for (int i = 0; i < len; i++) suffixes[i] = i;

        Arrays.sort(suffixes, (a, b) -> compareSuffixes(ref, a, b));

        int[] sa = new int[len];
        for (int i = 0; i < len; i++) sa[i] = suffixes[i];
        return sa;
    }

    private static int compareSuffixes(String ref, int i, int j) {
        while (i < ref.length() && j < ref.length()) {
            if (ref.charAt(i) != ref.charAt(j)) return ref.charAt(i) - ref.charAt(j);
            i++; j++;
        }
        return (i == ref.length()) ? -1 : 1;
    }

    // Build the Burrows-Wheeler Transform (BWT) from the suffix array
    public static String buildBurrowsWheeler(String text, int[] sa) {
        StringBuilder bwt = new StringBuilder();
        int n = text.length();

        for (int suffixIndex : sa) {
            int lastCharIndex = (suffixIndex + n - 1) % n;
            bwt.append(text.charAt(lastCharIndex));
        }

        return bwt.toString();
    }

    // Build first column: count occurrences of $, A, C, G, T
    public static int[] buildFirstColumn(String ref) {
        int[] firstColumn = new int[5];  // Add space for the '$' character
        for (int i = 0; i < ref.length(); i++) {
            switch (ref.charAt(i)) {
                case '$': firstColumn[0]++; break;  // Count the '$'
                case 'A': firstColumn[1]++; break;
                case 'C': firstColumn[2]++; break;
                case 'G': firstColumn[3]++; break;
                case 'T': firstColumn[4]++; break;
            }
        }
        return firstColumn;
    }

    // Build tally for A, C, G, T in the BWT string
    public static int[][] buildTally(String bwt) {
        int[][] tally = new int[4][bwt.length() + 1];
        for (int i = 1; i <= bwt.length(); i++) {
            char c = bwt.charAt(i - 1);

            tally[0][i] = tally[0][i - 1]; 
            tally[1][i] = tally[1][i - 1]; 
            tally[2][i] = tally[2][i - 1]; 
            tally[3][i] = tally[3][i - 1]; 

            switch (c) {
                case 'A': tally[0][i]++; break;
                case 'C': tally[1][i]++; break;
                case 'G': tally[2][i]++; break;
                case 'T': tally[3][i]++; break;
            }
        }

        return tally; 
    }

    // Serialize suffix array, BWT, first column, and tally to file
    public static void serialize(int[] sa, String bwt, int[] firstColumn, int[][] tally, String outFile) throws IOException {
        try (ObjectOutputStream file = new ObjectOutputStream(new FileOutputStream(outFile))) {
            file.writeObject(sa);
            file.writeObject(bwt);
            file.writeObject(firstColumn);  // Serialize first column (now with '$' count)
            file.writeObject(tally);
        }
    }
}
