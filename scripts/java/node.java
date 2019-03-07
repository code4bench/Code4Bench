//Do not run it, this will use by simAndRealFaults.java
class node{                       /* the tree is made up of these nodes */
    node pleft, pright;
    int linenum;
    public static int counterPrintedLineInsert =0 ;
    public static int counterPrintedLineChange =0 ;
    public static int counterPrintedLineDelete =0 ;
    public static String typeEdit = "";
    static final int freshnode = 0,
            oldonce = 1, newonce = 2, bothonce = 3, other = 4;

    int /* enum linestates */ linestate;
    String line;

    static node panchor = null;    /* symtab is a tree hung from this */

    node( String pline)
    {
        pleft = pright = null;
        linestate = freshnode;
       /* linenum field is not always valid */
        line = pline;
    }

    static node matchsymbol( String pline )
    {
        int comparison;
        node pnode = panchor;
        if ( panchor == null ) return panchor = new node( pline);
        for(;;) {
            comparison = pnode.line.compareTo(pline);
            if ( comparison == 0 ) return pnode;          /* found */

            if ( comparison < 0 ) {
                if ( pnode.pleft == null ) {
                    pnode.pleft = new node( pline);
                    return pnode.pleft;
                }
                pnode = pnode.pleft;
            }
            if ( comparison > 0 ) {
                if ( pnode.pright == null ) {
                    pnode.pright = new node( pline);
                    return pnode.pright;
                }
                pnode = pnode.pright;
            }
        }
       /* NOTE: There are return stmts, so control does not get here. */
    }

    /**
     * addSymbol(String pline) - Saves line into the symbol table.
     * Returns a handle to the symtab entry for that unique line.
     * If inoldfile nonzero, then linenum is remembered.
     */
    static node addSymbol( String pline, boolean inoldfile, int linenum )
    {
        node pnode;
        pnode = matchsymbol( pline );  /* find the node in the tree */
        if ( pnode.linestate == freshnode ) {
            pnode.linestate = inoldfile ? oldonce : newonce;
        } else {
            if (( pnode.linestate == oldonce && !inoldfile ) ||
                    ( pnode.linestate == newonce &&  inoldfile ))
                pnode.linestate = bothonce;
            else pnode.linestate = other;
        }
        if (inoldfile) pnode.linenum = linenum;
        return pnode;
    }

    /**
     * symbolIsUnique    Arg is a ptr previously returned by addSymbol.
     * --------------    Returns true if the line was added to the
     *                   symbol table exactly once with inoldfile true,
     *                   and exactly once with inoldfile false.
     */
    boolean symbolIsUnique()
    {
        return (linestate == bothonce );
    }

    /**
     * showSymbol        Prints the line to stdout.
     */
    void showSymbol()
    {
        if(typeEdit.equals("insert")){
            counterPrintedLineInsert++;
        }
        if(typeEdit.equals("change")){
            counterPrintedLineChange++;
        }
        if(typeEdit.equals("delete")){
            counterPrintedLineDelete++;
        }
//        System.out.println(line);
//        System.out.println("insert --> " + counterPrintedLineInsert);
//        System.out.println("change --> " + counterPrintedLineChange);
//        System.out.println("delete --> " + counterPrintedLineDelete);
    }
}
