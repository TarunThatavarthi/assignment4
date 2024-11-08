import java.io.*;

public class queryfm {
    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            System.err.println("Usage: java QueryFM <index.file> <query.fasta> <mode> <output.file>");
            return;
        }

        String indexFile = args[0];
        String queryFile = args[1];
        String mode = args[2];
        String outputFile = args[3];

        int[] suffixArray;
        char[] bwt;
        int[][] tallyTable;

        // Load index components
        try (DataInputStream in = new DataInputStream(new FileInputStream(indexFile))) {
            suffixArray = readSuffixArray(in);
            bwt = readBWT(in);
            tallyTable = readTallyTable(in);
        }

        // Perform backward search and write results
        // ... [Search logic not shown, implement backward search based on mode] ...
    }

    private static int[] readSuffixArray(DataInputStream in) throws IOException {
        int size = in.readInt();
        int[] suffixArray = new int[size];
        for (int i = 0; i < size; i++) suffixArray[i] = in.readInt();
        return suffixArray;
    }

    private static char[] readBWT(DataInputStream in) throws IOException {
        int size = in.readInt();
        char[] bwt = new char[size];
        for (int i = 0; i < size; i++) bwt[i] = in.readChar();
        return bwt;
    }

    private static int[][] readTallyTable(DataInputStream in) throws IOException {
        int rows = in.readInt();
        int[][] tallyTable = new int[rows][4];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < 4; j++) tallyTable[i][j] = in.readInt();
        }
        return tallyTable;
    }

    // Implement additional functions for backward search and result writing...
}
