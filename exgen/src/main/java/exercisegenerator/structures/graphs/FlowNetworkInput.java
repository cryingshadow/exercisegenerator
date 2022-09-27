package exercisegenerator.structures.graphs;

/**
 * Input object for flow network problems.
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
    public LabeledNode<N> sink;

    /**
     * The source of the flow network.
     */
    public LabeledNode<N> source;

    /**
     * Should residual graphs and flow networks be displayed in two columns?
     */
    public boolean twocolumns;

}
