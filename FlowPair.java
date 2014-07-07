/**
 * Pair used for flow networks. Just overrides the toString method.
 * @author Thomas Stroeder
 * @version 1.0
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
    public FlowPair(Integer first, Integer second) {
        super(first, second);
    }

    /* (non-Javadoc)
     * @see Pair#toString()
     */
    @Override
    public String toString() {
        if (GraphAlgorithms.ERIKA_MODE) {
            return (this.x > 0 ? this.x + "/" : "") + this.y;
        }
        return this.x + "/" + this.y;
    }

}
