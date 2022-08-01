package exercisegenerator;

/**
 * Pair used for flow networks. Just overrides the toString method.
 * @author Thomas Stroeder
 * @version 1.0.1
 */
public class FlowPair extends Pair<Integer, Integer> {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -4603924585579470577L;

    /**
     * @param first The flow component.
     * @param second The capacity component.
     */
    public FlowPair(final Integer first, final Integer second) {
        super(first, second);
    }

    /* (non-Javadoc)
     * @see Pair#toString()
     */
    @Override
    public String toString() {
        switch (DSALExercises.TEXT_VERSION) {
            case ABRAHAM:
                return (this.x > 0 ? this.x + "/" : "") + this.y;
            case GENERAL:
                return this.x + "/" + this.y;
            default:
                throw new IllegalStateException("Unkown text version!");
        }
    }

}
