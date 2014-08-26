/**
 * Input object for flow network problems.
 * @author Thomas Stroeder
 * @version 1.0.1
 * @param <N> Node label type.
 * @param <E> Edge label type.
 */
public class FlowNetworkInput<N, E> {

    /**
     * The flow network.
     */
    public Graph<N, E> graph;

    /**
     * Multiplier for node distances.
     */
    public double multiplier;

    /**
     * The sink of the flow network.
     */
    public Node<N> sink;

    /**
     * The source of the flow network.
     */
    public Node<N> source;

    /**
     * Should residual graphs and flow networks be displayed in two columns?
     */
    public boolean twocolumns;

}
