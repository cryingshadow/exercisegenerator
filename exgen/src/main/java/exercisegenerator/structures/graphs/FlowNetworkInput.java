package exercisegenerator.structures.graphs;

/**
 * Input object for flow network problems.
 * @param <V> Vertex label type.
 * @param <E> Edge label type.
 */
public class FlowNetworkInput<V, E> {

    /**
     * The flow network.
     */
    public Graph<V, E> graph;

    /**
     * Multiplier for vertex distances.
     */
    public double multiplier;

    /**
     * The sink of the flow network.
     */
    public Vertex<V> sink;

    /**
     * The source of the flow network.
     */
    public Vertex<V> source;

    /**
     * Should residual graphs and flow networks be displayed in two columns?
     */
    public boolean twocolumns;

}
