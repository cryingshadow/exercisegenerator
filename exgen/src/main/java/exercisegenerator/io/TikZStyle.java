package exercisegenerator.io;

public enum TikZStyle {

    ARRAY("[node/.style={rectangle,draw=black,thick,inner sep=5pt,font={\\Large}},node distance=0.25 and 0]"),

    ARRAY_WITH_INDICES("[node/.style={rectangle,draw=black,thick,inner sep=5pt,font={\\Large}},node distance=0.5 and 0]"),

    BORDERLESS(
        "[node/.style={draw=none,thick,inner sep=5pt, text width = 10cm,font={\\Large}}, node distance=0.25 and 0]"
    ),

    BTREE(
        "[every tree node/.style={rounded corners,draw=black,thick,inner sep=5pt}, "
        + "sibling distance=10pt, level distance=30pt, edge from parent/.style="
        + "{draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]"
    ),

    CLAUSE_SET("[node distance=0.5 and 1]"),

    EDGE_HIGHLIGHT_STYLE("[p, bend right = 10, very thick, red]"),

    EDGE_STYLE("[p, bend right = 10]"),

    EMPTY(""),

    GRAPH(
        "[scale=2.4, node/.style={circle,draw=black,thin,inner sep=5pt}, >=stealth, "
        + "p/.style={->, thin, shorten <=2pt, shorten >=2pt}]"
    ),

    POINTSET("[framed,draw=black]"),

    RED_BLACK_TREE(
        "[every tree node/.style={circle,draw=gray,thick,inner sep=5pt}, "
        + "b/.style={rectangle,draw=black,thick,inner sep=5pt}, "
        + "r/.style={circle,draw=gray,thick,inner sep=5pt}, "
        + "bb/.style={rectangle,general shadow={draw=black,shadow xshift=.5ex,shadow yshift=.5ex},draw=black,fill=white,thick,inner sep=5pt}, "
        + "rb/.style={circle,draw=black,dashed,thick,inner sep=5pt}, "
        + "sibling distance=10pt, level distance=30pt, "
        + "edge from parent/.style={draw,edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]"
    ),

    SYM_EDGE_HIGHLIGHT_STYLE("[p, very thick, red]"),

    SYM_EDGE_STYLE("[p]"),

    SYM_GRAPH(
        "[scale=2.4, node/.style={circle,draw=black,thin,inner sep=5pt}, "
        + "p/.style={thin}]"
    ),

    TREE(
        "[every tree node/.style={circle,draw=black,thick,inner sep=5pt}, "
        + "sibling distance=10pt, level distance=30pt, edge from parent/.style="
        + "{draw, edge from parent path={(\\tikzparentnode) -- (\\tikzchildnode)}}]"
    );

    public final String style;

    private TikZStyle(final String s) {
        this.style = s;
    }

}