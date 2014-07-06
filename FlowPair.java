/**
 * Pair used for flow networks. Just overrides the toString method.
 * @author Thomas Ströder
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
        return "" + this.x + "/" + this.y;
    }

}
