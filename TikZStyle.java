
/**
 * Styles for TikZ environments.
 * @author cryingshadow
 * @version $Id$
 */
enum TikZStyle {

    /**
     * Array style.
     */
    ARRAY("[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]"),

    /**
     * Borderless Array style (for String-Arrays).
     */
    BORDERLESS("[node/.style={draw=none,thick,inner sep=5pt, text width = 10cm}, node distance=0.25 and 0]"),

    /**
     * B-tree style.
     */
    BTREE(
        "[every tree node/.style={rounded corners,draw=black,thick,inner sep=5pt}, "
        + "sibling distance=10pt, level distance=30pt, edge from parent/.style="
        + "{draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]"
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
    private TikZStyle(String s) {
        this.style = s;
    }

}