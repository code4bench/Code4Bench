//Do not run it, this will use by simAndRealFaults.java
import java.io.*;

/** This is the info kept per-file.     */
class fileInfo {

    static final int MAXLINECOUNT = 20000;

    DataInputStream file;  /* File handle that is open for read.  */
    public int maxLine;  /* After input done, # lines in file.  */
    node symbol[]; /* The symtab handle of each line. */
    int other[]; /* Map of line# to line# in other file */
                                /* ( -1 means don't-know ).            */
        /* Allocated AFTER the lines are read. */

    /**
     * Normal constructor with one filename; file is opened and saved.
     */
    fileInfo( String filename ) {
        symbol = new node [ MAXLINECOUNT+2 ];
        other  = null;    // allocated later!
        try {
            file = new DataInputStream(
                    new FileInputStream( filename));
        } catch (IOException e) {
            System.err.println("Diff can't read file " +
                    filename );
            System.err.println("Error Exception was:" + e );
            System.exit(1);
        }
    }
    // This is done late, to be same size as # lines in input file.
    void alloc() {
        other  = new int[symbol.length + 2];
    }

    void println(String str){
//        System.out.println(str);
    }
};