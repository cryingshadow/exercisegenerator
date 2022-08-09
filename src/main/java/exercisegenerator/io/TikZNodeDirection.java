package exercisegenerator.io;

public enum TikZNodeDirection {

    ABOVE("above"),

    ABOVE_LEFT("above left"),

    ABOVE_RIGHT("above right"),

    BELOW("below"),

    BELOW_LEFT("below left"),

    BELOW_RIGHT("below right"),

    LEFT("left"),

    RIGHT("right");

    public final String tikz;

    private TikZNodeDirection(final String tikz) {
        this.tikz = tikz;
    }

}
