import java.io.*;
import java.util.*;

/**
 * Programm for creating planar graphs with node positions according to a grid.
 * @author Florian Corzilius
 * @version $Id$
 */
public class GridGraph {

    /**
     * The number of columns in the grid according which the graph this exercise considers is ordered (must be odd).
     */
    private static int mNumOfColumnsInGrid = 7;

    /**
     * The number of adjacent nodes to store in the sparse version of the adjacency matrix for the nodes it considers. 
     * This sparse version is used to create the graph this exercise considers.
     */
    private static int mNumOfNeighborsInSparseAdjacencyMatrix = 6;

    /**
     * The number of rows in the grid according which the graph this exercise considers is ordered (must be odd).
     */
    private static int mNumOfRowsInGrid = 5;

    /**
     * Takes a sparse version of the adjacency matrix and constructs the according graph, which has not more than 35 nodes
     * and each node has a degree not greater than 8. The tex-code to present the resulting graph is then written to the file given 
     * by the fourth argument. The results from applying the given operation on this graph are written to the file given by the last
     * argument.
     * 
     * @param graph An empty graph object.
     * @param sparseAdjacencyMatrix A sparse version of the adjacency matrix in order to construct the according graph 
     *                 (elements in a row are separated by "," and rows are separated by line breaks). Sparse means, that if we consider the nodes
     *                 being ordered in a 5x7 grid, we only store every second node, starting from the first node in the first row and traversing
     *                 row wise (i.e., than the 3. node in the first row, than the 5. node in the first row, than the 7. node in the first row, than
     *                 the 2. node in the second row, ..). Furthermore, sparse means, that we only store adjacencies to not more than 6 neighbors 
     *                 (according to the grid) of a node, more precisely, those six which are positioned north, east, south, southwest, west and 
     *                 northwest of the node. If the entry in the sparse version of the adjacency matrix is 
     *                          * 1, then there is an outgoing edge
     *                          * -1, then there is an ingoing edge
     *                          * 2, then there is an outgoing and ingoing edge
     *                          * 0, no edge
     *                 to the corresponding node.
     *
     *                 Example: "x,2,2,x,x,x
     *                           x,2,2,2,2,x
     *                           x,2,2,2,2,x
     *                           x,x,2,2,2,x
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,2,x,x,x
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,x,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,x,x,x,x
     *                           2,2,x,x,2,2
     *                           2,2,x,x,2,2
     *                           2,x,x,x,2,2"
     *                  This is the graph where every of the 35 nodes in the grid is connected to all it's neighbors. At the positions with "x" 
     *                  the values do not influence the graph as they belong to potential neighbors not being in the grid.
     * @param operation The name of the operation to apply to the graph.
     * @param writer The writer for the solution.
     * @param writerSpace The writer for the tree to start with (the one reached after the <code>construction</code> 
     *                    operations). May be null if this tree should not be displayed separately.
     */
    public static void gridGraph (
        GridGraph graph,
        int[][] sparseAdjacencyMatrix,
        String operation,
        BufferedWriter writer,
        BufferedWriter writerSpace
    ) throws IOException {
        graph.createGraph(sparseAdjacencyMatrix);
        switch(operation) {
        case "find_sccs":
            if (writerSpace != null) {
                writerSpace.write(
                    "Geben Sie alle starken Zusammenhangskomponenten im folgenden Graph an. F\\\"ur jede dieser "
                    + "starken Zusammenhangskomponenten reicht es die Menge der Knoten anzugeben, die darin auftreten."
                );
                writerSpace.newLine();
                graph.printGraph(writerSpace, false);
            }
            graph.printSCCs(writer, false, false);
            writer.newLine();
            break;
        case "sharir":
            if (writerSpace != null) {
                writerSpace.write(
                    "Wenden Sie Sharir's Algorithmus an (siehe Folien zur Vorlesung) um die starken" 
                    + " Zusammenhangskomponenten des folgenden Graphen zu finden. Geben Sie das Array \\texttt{color}"
                    + " und den Stack \\texttt{S} nach jeder Schleifeniteration der ersten und zweiten Phase (also nach "
                    + "Zeile 17 und nach Zeile 22) an, falls \\texttt{DFS1} bzw. \\texttt{DFS2} ausgef\\\"uhrt wurde."
                    + "Geben Sie zudem das Array \\texttt{scc} nach jeder Schleifeniteration der zweiten Phase (also "
                    + "nach Zeile 22) an, falls \\texttt{DFS2} ausgef\\\"uhrt wurde. Nehmen Sie hierbei an, dass \\texttt{scc}"
                    + " initial mit Nullen gef\\\"ullt ist."
                );
                writerSpace.newLine();
                graph.printGraph(writerSpace, false);
            }
            graph.printSCCs(writer, false, true) ;
            writer.newLine();
            break;
        case "topologicSort":
            graph.printTopologicalOrder(writerSpace, writer, false);
            break;
        default:
            
        }
    }

    /// The adjacency matrix of the graph used in this exercise.
    private boolean[][] mAdjacencyMatrix;

    /**
     * Creates an SCC exercise with the graph as given in the sparse version of the adjacency matrix.
     */
    public GridGraph() {
        int numOfNodes = GridGraph.mNumOfRowsInGrid * GridGraph.mNumOfColumnsInGrid;
        this.mAdjacencyMatrix = new boolean[numOfNodes][numOfNodes];
    }

    /**
     * Creates the graph with node positioned according to a grid and the given sparse version of the adjacency matrix of
     * the graph to create.
     * @param sparseAdjacencyMatrix The sparse version of the adjacency matrix of the graph to create.
     */
    public void createGraph(int[][] sparseAdjacencyMatrix) {
        for (int i = 0; i < this.numOfNodesInSparseAdjacencyMatrix(); i++) {
            int nodeNum = 2*i;
            // north
            if (nodeNum > this.numOfColumnsInGrid()) {
                this.addEdges(nodeNum, nodeNum-this.numOfColumnsInGrid(), sparseAdjacencyMatrix[i][0]);
            }
            // east
            if (nodeNum % this.numOfColumnsInGrid() < this.numOfColumnsInGrid() - 1) {
                this.addEdges(nodeNum, nodeNum+1, sparseAdjacencyMatrix[i][1]);
            }
            // south
            if (nodeNum + this.numOfColumnsInGrid() < this.numOfAllNodes() - 1) {
                this.addEdges(nodeNum, nodeNum+this.numOfColumnsInGrid(), sparseAdjacencyMatrix[i][2]);
            }
            // south-west
            if (nodeNum + this.numOfColumnsInGrid() < this.numOfAllNodes() - 1 && nodeNum % this.numOfColumnsInGrid() > 0) {
                this.addEdges(nodeNum, nodeNum+this.numOfColumnsInGrid()-1, sparseAdjacencyMatrix[i][3]);
            }
            // west
            if (nodeNum % this.numOfColumnsInGrid() > 0) {
                this.addEdges(nodeNum, nodeNum-1, sparseAdjacencyMatrix[i][4]);
            }
            // north-west
            if (nodeNum > this.numOfColumnsInGrid() && nodeNum % this.numOfColumnsInGrid() > 0) {
                this.addEdges(nodeNum, nodeNum-this.numOfColumnsInGrid()-1, sparseAdjacencyMatrix[i][5]);
            }
        } 
    }

    public boolean isLegalEntryForSparseAdjacencyMatrix(int entry) {
        return (entry >= -1 && entry <= 2);
    }

    /**
     * The necessary positions are those being 2:
     *                           x,2,2,x,x,x
     *                           x,2,2,2,2,x
     *                           x,2,2,2,2,x
     *                           x,x,2,2,2,x
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,2,x,x,x
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,x,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,2,2,2,2
     *                           2,2,x,x,x,x
     *                           2,2,x,x,2,2
     *                           2,2,x,x,2,2
     *                           2,x,x,x,2,2
     */
    public boolean isNecessarySparseMatrixEntry(int i, int j) {
        if (j < 0 || j > 5 ) {
            return false;
        }
        if (i < 0 || i > 17 ) {
            return false;
        }
        switch(i){
        case 0:
            return (j == 1 || j == 2);
        case 1:
            return (j >= 1 && j <= 4);
        case 2:
            return (j >= 1 && j <= 4);
        case 3:
            return (j >= 2 && j <= 4);
        case 7:
            return j <= 2;
        case 10:
            return j != 1;
        case 14:
            return j <= 1;
        case 15:
            return (j <= 1 || j >= 4);
        case 16:
            return (j <= 1 || j >= 4);
        case 17:
            return (j == 0 || j >= 4);
        default:
            return true;
        }
    }

    public boolean nodeHasAdjacentNodes(int nodeNumber) {
        for(int i = 0; i < this.numOfAllNodes(); i++) {
            if (this.mAdjacencyMatrix[nodeNumber][i]) {
                return true;
            }
        }
        for(int i = 0; i < this.numOfAllNodes(); i++) {
            if (this.mAdjacencyMatrix[i][nodeNumber]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of all nodes, that includes the nodes which have no adjacent node and are considered as not existing in this exercise.
     */
    public int numOfAllNodes() {
        return GridGraph.mNumOfRowsInGrid * GridGraph.mNumOfColumnsInGrid;
    }

    public int numOfColumnsInGrid() {
        return GridGraph.mNumOfColumnsInGrid;
    }

    public int numOfNeighborsInSparseAdjacencyMatrix() {
        return GridGraph.mNumOfNeighborsInSparseAdjacencyMatrix;
    }

    public int numOfNodesInSparseAdjacencyMatrix() {
        return (GridGraph.mNumOfRowsInGrid * GridGraph.mNumOfColumnsInGrid + 1) / 2;
    }

    public int numOfRowsInGrid() {
        return GridGraph.mNumOfRowsInGrid;
    }
    
    public String nodeName(int n) {
        System.out.println("bla0");
        int c = 1;
        for (int i = 0; i < numOfAllNodes(); i++) {
            if (i == n) {
                break;
            }
            if (nodeHasAdjacentNodes(i)) {
                c++;
            }
            
        }
        System.out.println("bla0x");
        return "" + c;
    }

    /**
     * Adds the entries into the adjacency matrix if the graph of this exercise, between the nodes of the given numbers.
     * @param from The first node connected to the edge(s) to create.
     * @param to The second node connected to the edge(s) to create.
     * @param type 1, if only an edge from the first node to the second should be inserted;
     *             -1, if only an edge from the second node to the first should be inserted;
     *             2, if an edge from the first node to the second should and an edge from the second to the first node should be inserted;
     *             otherwise, no edge is added.
     */
    void addEdges(int from, int to, int type) {
        switch (type) {
            case -1:
                this.mAdjacencyMatrix[to][from] = true;
                this.mAdjacencyMatrix[from][to] = false;
                return;
            case 1:
                this.mAdjacencyMatrix[to][from] = false;
                this.mAdjacencyMatrix[from][to] = true;
                return;
            case 2:
                this.mAdjacencyMatrix[to][from] = true;
                this.mAdjacencyMatrix[from][to] = true;
                return;
            default:
                this.mAdjacencyMatrix[to][from] = false;
                this.mAdjacencyMatrix[from][to] = false;
                return;
        }
    }

    int dfsTopologicOrdering(int start, int[] colors, int topoNum, Integer[] topo) throws IOException {
        colors[start] = 1;
        int[] neighbors = this.getNeighbors(start);
        for(int index = 0; index < neighbors.length; ++index){
            System.out.println("Start: " + start + ", Neighbor: " + neighbors[index]);
            if(colors[neighbors[index]]==1){
                throw new IOException("The given graph is cyclic!");
            }
            if(colors[neighbors[index]] == 0){
                topoNum = this.dfsTopologicOrdering(neighbors[index], colors, topoNum, topo);
            }
        }
        topoNum +=1;
        topo[start] = topoNum;
        System.out.println("Set topo of " + start + " to " + topoNum);
        colors[start] = 2;
        return topoNum;
    }

    void dfsWalk(int from, int[] color, List<Integer> result, int ccNum, int[] cc, int directions, int[] S, int[] lastOfS) {
        if (color == null) {
            color = new int[this.numOfAllNodes()];
        }
        color[from] = 1;
        if (result != null) {
            result.add(from);
        }
        if (cc != null) {
            cc[from] = ccNum;
        }
        for (int i = 0; i < this.numOfAllNodes(); i++) {
            if (color[i] == 0 && ((directions >= 0 && this.mAdjacencyMatrix[from][i]) || (directions <= 0 && this.mAdjacencyMatrix[i][from]))) {
                this.dfsWalk(i, color, result, ccNum, cc, directions, S, lastOfS);
            }
        }
        color[from] = 2;
        if (S != null) {
            lastOfS[0]++;
            S[lastOfS[0]] = from;
        }
    }

    int[] findSCCs(BufferedWriter writer, boolean write) throws IOException {
        int[] result = new int[this.numOfAllNodes()];
        for (int w = 0; w < this.numOfAllNodes(); w++) {
            result[w] = -1;
        }
                
        // get the connected components
        int[] color = new int[this.numOfAllNodes()];
        
        // split all connected components into strongly connected components
        int sccNum = 0;
        int[] lastOfS = new int[1]; // Array storing only one int, as it is not possible to call primitive types in java by reference..
        lastOfS[0] = -1;
        int[] S = new int[this.numOfAllNodes()];
        int[] colorA = new int[this.numOfAllNodes()];
        // phase 1
        if(write) {
            writer.newLine();
            writer.write("Phase 1:");
            writer.newLine();
            writer.newLine();
            writer.write("\\medskip");
            writer.newLine();
        }
        for (int w = 0; w < this.numOfAllNodes(); w++) {
            if (colorA[w] == 0) {
                this.dfsWalk(w, colorA, null, 0, null, 1, S, lastOfS);
                
                if(write && nodeHasAdjacentNodes(w)) {
                    printS(writer, S, lastOfS);
                    printColor(writer, colorA);
                    writer.write("\\medskip");
                    writer.newLine(); 
                }
            }
        }
        // phase 2
        if(write) {
            writer.write("\\medskip");
            writer.newLine();
            writer.write("Phase 2:");
            writer.newLine();
            writer.newLine();
            writer.write("\\medskip");
            writer.newLine();
        }
        int[] colorB = new int[this.numOfAllNodes()];
        System.out.println("blaaaaaaaaaaaaaaaaaa4");
        while (lastOfS[0] > 0) {
            Integer v = S[lastOfS[0]];
            lastOfS[0]--;
            //System.out.println("current element from stack: " + v + " with color " + colorB[v]);
            if (colorB[v] == 0) {
                List<Integer> walk = new ArrayList<Integer>();
                this.dfsWalk(v, colorB, walk, 0, null, -1, null, null);
                for (Integer node : walk) {
                    result[node] = sccNum;
                }
                sccNum++;
                if(write && nodeHasAdjacentNodes(v)) {
                    printS(writer, S, lastOfS);
                    printColor(writer, colorB);
                    printScc(writer, result);
                    writer.write("\\medskip");
                    writer.newLine(); 
                }
            }
        }
        System.out.println("blaaaaaaaaaaaaaaaaaa4x");
        return result;
    }
    
    void printColor(BufferedWriter writer, int[] color) throws IOException {
    System.out.println("bla1");
        boolean firstWritten = false;
        writer.write("color: ");
        for (int i = 0; i < numOfAllNodes(); i++) {
            if (nodeHasAdjacentNodes(i)) {
                if (firstWritten) {
                    writer.write(", ");
                } else {
                    firstWritten = true;
                }
                writer.write("(" + nodeName(i) + ", ");
                if (color[i] == 0) {
                    writer.write("w)");
                } else if (color[i] == 1) {
                    writer.write("g)"); 
                } else {
                    writer.write("s)"); 
                }
            }
        }
        writer.newLine();
        writer.newLine();  
        System.out.println("bla1x");
    }
    
    void printS(BufferedWriter writer, int[] S, int[] lastOfS) throws IOException {
    System.out.println("bla2");
        boolean firstWritten = false;
        writer.write("S: ");
        for (int i = 0; i < lastOfS[0]; i++) {
            if (nodeHasAdjacentNodes(i)) {
                if (firstWritten) {
                    writer.write(", ");
                } else {
                    firstWritten = true;
                }
                writer.write("" + nodeName(S[i]));
            }
        }
        writer.newLine();
        writer.newLine();
        System.out.println("bla2x");
    }
    
    void printScc(BufferedWriter writer, int[] scc) throws IOException {
        System.out.println("bla3");
        boolean firstWritten = false;
        writer.write("scc: ");
        for (int i = 0; i < numOfAllNodes(); i++) {
            if (nodeHasAdjacentNodes(i)) {
                if (firstWritten) {
                    writer.write(", ");
                } else {
                    firstWritten = true;
                }
                writer.write("(" + nodeName(i) + ", " + nodeName(scc[i]) + ")");
            }
        }
        writer.newLine();
        writer.newLine();  
        System.out.println("bla3x");
    }

    int[] getNeighbors(int nodeIndex){
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(int index = 0; index < this.mAdjacencyMatrix[nodeIndex].length; ++index){
            if(this.mAdjacencyMatrix[nodeIndex][index] == true){
                result.add(index);
            }
        }
        int[] ret = new int[result.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = result.get(i).intValue();
        }
        return ret;
    }

    Integer[] topologicSort() throws IOException {
        Integer[] result = new Integer[this.numOfAllNodes()];
        Arrays.fill(result, null);
        int[] color = new int[this.numOfAllNodes()];
        Arrays.fill(color, 0);
        int topoNum = 0;
        for(int nodeIndex = 0; nodeIndex < this.numOfAllNodes(); ++nodeIndex){
            if(color[nodeIndex] == 0 && this.nodeHasAdjacentNodes(nodeIndex)){
                //try{
                    topoNum = this.dfsTopologicOrdering(nodeIndex, color, topoNum, result);
                //}
                //catch ( IOException e ){
                //    System.out.println("Found cycle!");
                //}
            }
        }
        return result;
    }

    /**
     * Prints this AVL-tree right under the given headline.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printGraph(BufferedWriter writer, boolean withSingletons) throws IOException {
        TikZUtils.printBeginning(TikZUtils.CENTER, writer);
        TikZUtils.printTikzBeginning(TikZStyle.GRAPH, writer);
        // print the nodes
        writer.write("% The nodes:");
        writer.newLine();
        for (int i = 0; i < this.numOfAllNodes(); i++) {
            if (withSingletons || this.nodeHasAdjacentNodes(i)) {
                writer.write("\\node[node] (" + i + ") at (" + (i % this.numOfColumnsInGrid()) + "," + ((this.numOfAllNodes()-1-i) / this.numOfColumnsInGrid()) + ") {" + nodeName(i) + "};");
                writer.newLine();
            }
        }
        writer.write("% The edges:");
        writer.newLine();
        for (int i = 0; i < this.mAdjacencyMatrix.length; i++) {
            for (int j = i+1 ; j < this.mAdjacencyMatrix[i].length; j++) {
                if (this.mAdjacencyMatrix[i][j] && this.mAdjacencyMatrix[j][i]) {
                    writer.write("\\path[p, bend right=15] (" + i + ") edge (" + j + ");");
                    writer.newLine();
                    writer.write("\\path[p, bend right=15] (" + j + ") edge (" + i + ");");
                    writer.newLine();
                } else if (this.mAdjacencyMatrix[i][j]) {
                    writer.write("\\path[p] (" + i + ") edge (" + j + ");");
                    writer.newLine();
                } else if (this.mAdjacencyMatrix[j][i]) {
                    writer.write("\\path[p] (" + j + ") edge (" + i + ");");
                    writer.newLine();
                }
            }
        }
        // print the edges
        TikZUtils.printTikzEnd(writer);
        TikZUtils.printEnd(TikZUtils.CENTER, writer);
    }

    /**
     * Prints all SCCs in the graph of this exercise.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printSCCs(BufferedWriter writer, boolean withSingletons, boolean write) throws IOException {
        int[] sccs = this.findSCCs(writer, write);
        int sccNum = -1;
        int checkedNodes = 0;
        int i = 0;
        boolean newScc = true;
        writer.write("Der gegebene Graph hat folgende starken Zusammenhangskomponenten:");
        writer.newLine();
        writer.newLine();
        writer.write("\\[");
        writer.newLine();
        writer.write("    \\begin{array}{l}");
        writer.newLine();
        while (checkedNodes < this.numOfAllNodes()) {
            if (sccs[i] == sccNum) {
                if (withSingletons || this.nodeHasAdjacentNodes(i)) {
                    if (newScc) {
                        writer.write("        \\{ " + nodeName(i));
                    } else {
                        writer.write(",\\ " + nodeName(i));
                    }
                    newScc = false;
                }
                checkedNodes++;
            }
            i++;
            if (i == this.numOfAllNodes() || checkedNodes == this.numOfAllNodes()) {
                if (!newScc) {
                    writer.write("\\}");
                    writer.write("\\\\");
                    writer.newLine();
                }
                i = 0;
                sccNum++;
                newScc = true;
            }
        }
        writer.write("    \\end{array}");
        writer.newLine();
        writer.write("\\]");
        writer.newLine();
        writer.newLine();
    }

    /**
     * Prints the topological order of the graph of this exercise.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printTopologicalOrder(BufferedWriter exerciseWriter, BufferedWriter solutionWriter, boolean withSingletons) throws IOException {
        Integer[] nodeValues = this.topologicSort();
        if (exerciseWriter != null) {
            exerciseWriter.write(
                              "Geben Sie eine topologische Sortierung des folgenden Graphen an. Daf\\\"ur reicht es, "
                              + "eine geordnete Liste der Knoten mit dem dazugeh\\\"origen Topologiewert in Klammern anzugeben. "
                              + "Die Tiefensuche ber\\\"ucksichtigt bei mehreren Kindern diese in aufsteigender Reihenfolge (ihrer Schl\\\"ussel). "
                              + "Desweiteren ist jedes Array, welches Knoten beinhaltet aufsteigend nach deren Schl\\\"usseln sortiert."
                              );
            exerciseWriter.newLine();
            this.printGraph(exerciseWriter, false);
        }
        solutionWriter.write("Der gegebene Graph hat die folgende topologische Sortierung:\\\\");
        solutionWriter.newLine();
        solutionWriter.newLine();
        int min = 1;
        int first = 0;
        for(int index = 0; index < nodeValues.length; ++index){
            if(nodeValues[index] != null && nodeValues[index] == min){
                solutionWriter.write(index + "("+ nodeValues[index] + ")");
                first = index;
                break;
            }
        }
        while(min < nodeValues.length){
            for(int index = 0; index < nodeValues.length; ++index){
                if(nodeValues[index] != null && nodeValues[index] == min && first != index){
                    solutionWriter.write(", " + index + "("+ nodeValues[index] + ")");
                }
            }
            ++min;
        }
        solutionWriter.write("\\\\");
        solutionWriter.newLine();
    }

}
