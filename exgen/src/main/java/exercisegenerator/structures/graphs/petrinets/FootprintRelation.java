package exercisegenerator.structures.graphs.petrinets;

public enum FootprintRelation {

    EXCLUDED("\\#"), FOLLOWED("\\rightarrow"), MUTUAL("\\parallel"), PRECDEDED("\\leftarrow");

    public final String symbol;

    private FootprintRelation(final String symbol) {
        this.symbol = symbol;
    }

    public FootprintRelation merge(final FootprintRelation relation) {
        switch (this) {
        case EXCLUDED:
            return relation;
        case FOLLOWED:
            switch (relation) {
            case MUTUAL:
            case PRECDEDED:
                return MUTUAL;
            default:
                return this;
            }
        case MUTUAL:
            return MUTUAL;
        case PRECDEDED:
            switch (relation) {
            case FOLLOWED:
            case MUTUAL:
                return MUTUAL;
            default:
                return this;
            }
        default:
            throw new IllegalStateException("Unknown relation detected!");
        }
    }

}
