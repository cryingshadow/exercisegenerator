package exercisegenerator.io;

/**
 * Styles for TikZ environments.
 */
public enum TikZStyle {

    /**
     * Array style.
     */
    ARRAY("[node/.style={rectangle,draw=black,thick,inner sep=5pt,font={\\Large}},node distance=0.25 and 0]"),

    /**
     * Borderless Array style (for String-Arrays).
     */
    BORDERLESS("[node/.style={draw=none,thick,inner sep=5pt, text width = 10cm,font={\\Large}}, node distance=0.25 and 0]"),

    /**
     * B-tree style.
     */
    BTREE(
        "[every tree node/.style={rounded corners,draw=black,thick,inner sep=5pt}, "
        + "sibling distance=10pt, level distance=30pt, edge from parent/.style="
        + "{draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]"
    ),

    /**
     * Graph style.
     */
    GRAPH(
        "[scale=2.4, node/.style={circle,draw=black,thin,inner sep=5pt}, >=stealth, "
        + "p/.style={->, thin, shorten <=2pt, shorten >=2pt}]"
    ),

    /**
     * Graph style.
     */
    POINTSET(
    "[framed,draw=black]"
    ),

    RED_BLACK_TREE(
        "[every tree node/.style={circle,draw=gray,thick,inner sep=5pt}, "
        + "b/.style={rectangle,draw=black,thick,inner sep=5pt}, "
        + "r/.style={circle,draw=gray,thick,inner sep=5pt}, "
        + "bb/.style={rectangle,general shadow={draw=black,shadow xshift=.5ex,shadow yshift=.5ex},draw=black,fill=white,thick,inner sep=5pt}, "
        + "rb/.style={circle,draw=black,dashed,thick,inner sep=5pt}, "
        + "sibling distance=10pt, level distance=30pt, "
        + "edge from parent/.style={draw,edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]"
    ),

    /**
     * Symmetric graph style.
     */
    SYM_GRAPH(
        "[scale=2.4, node/.style={circle,draw=black,thin,inner sep=5pt}, "
        + "p/.style={thin}]"
    ),

    /**
     * Tree style.
     */
    TREE(
        "[every tree node/.style={circle,draw=black,thick,inner sep=5pt}, "
        + "sibling distance=10pt, level distance=30pt, edge from parent/.style="
        + "{draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]"
    );

    /**
     * The style definition.
     */
    final String style;

    /**
     * Creates a TikZStyle with the specified style definition.
     * @param s The style definition.
     */
    private TikZStyle(final String s) {
        this.style = s;
    }

}