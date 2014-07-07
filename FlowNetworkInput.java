/**
 * Input object for flow network problems.
 * @author Thomas Stroeder
 * @version 1.0
 * @param <N> Node label type.
 * @param <E> Edge label type.
 */
public class FlowNetworkInput<N, E> {

    /**
     * The flow network.
     */
    public Graph<N, E> graph;

    /**
     * The source of the flow network.
     */
    public Node<N> source;

    /**
     * The sink of the flow network.
     */
    public Node<N> sink;

    /**
     * Multiplier for node distances.
     */
    public double multiplier;

}
