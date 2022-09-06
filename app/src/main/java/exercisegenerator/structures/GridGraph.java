package exercisegenerator.structures;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;

/**
 * Programm for creating planar graphs with node positions according to a grid.
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

    /// The adjacency matrix of the graph used in this exercise.
    private final boolean[][] mAdjacencyMatrix;

    /**
     * Creates an SCC exercise with the graph as given in the sparse version of the adjacency matrix.
     */
    public GridGraph() {
        final int numOfNodes = GridGraph.mNumOfRowsInGrid * GridGraph.mNumOfColumnsInGrid;
        this.mAdjacencyMatrix = new boolean[numOfNodes][numOfNodes];
    }

    /**
     * Creates the graph with node positioned according to a grid and the given sparse version of the adjacency matrix of
     * the graph to create.
     * @param sparseAdjacencyMatrix The sparse version of the adjacency matrix of the graph to create.
     */
    public void createGraph(final int[][] sparseAdjacencyMatrix) {
        for (int i = 0; i < this.numOfNodesInSparseAdjacencyMatrix(); i++) {
            final int nodeNum = 2*i;
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

    public boolean isLegalEntryForSparseAdjacencyMatrix(final int entry) {
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
    public boolean isNecessarySparseMatrixEntry(final int i, final int j) {
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

    public boolean nodeHasAdjacentNodes(final int nodeNumber) {
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

    public String nodeName(final int n) {
        if (n < 0) {
            return "0";
        }
        int c = 1;
        for (int i = 0; i < this.numOfAllNodes(); i++) {
            if (i == n) {
                break;
            }
            if (this.nodeHasAdjacentNodes(i)) {
                c++;
            }

        }
        return "" + c;
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

    /**
     * Prints this AVL-tree right under the given headline.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public void printGraph(final BufferedWriter writer, final boolean withSingletons) throws IOException {
        TikZUtils.printBeginning(TikZUtils.CENTER, writer);
        TikZUtils.printTikzBeginning(TikZStyle.GRAPH, writer);
        // print the nodes
        writer.write("% The nodes:");
        Main.newLine(writer);
        for (int i = 0; i < this.numOfAllNodes(); i++) {
            if (withSingletons || this.nodeHasAdjacentNodes(i)) {
                if (withSingletons) {
                    writer.write("\\node[node] (" + i + ") at (" + (i % this.numOfColumnsInGrid()) + "," + ((this.numOfAllNodes()-1-i) / this.numOfColumnsInGrid()) + ") {" + i + "};");
                } else {
                    writer.write("\\node[node] (" + i + ") at (" + (i % this.numOfColumnsInGrid()) + "," + ((this.numOfAllNodes()-1-i) / this.numOfColumnsInGrid()) + ") {" + this.nodeName(i) + "};");
                }
                Main.newLine(writer);
            }
        }
        writer.write("% The edges:");
        Main.newLine(writer);
        for (int i = 0; i < this.mAdjacencyMatrix.length; i++) {
            for (int j = i+1 ; j < this.mAdjacencyMatrix[i].length; j++) {
                if (this.mAdjacencyMatrix[i][j] && this.mAdjacencyMatrix[j][i]) {
                    writer.write("\\path[p, bend right=15] (" + i + ") edge (" + j + ");");
                    Main.newLine(writer);
                    writer.write("\\path[p, bend right=15] (" + j + ") edge (" + i + ");");
                    Main.newLine(writer);
                } else if (this.mAdjacencyMatrix[i][j]) {
                    writer.write("\\path[p] (" + i + ") edge (" + j + ");");
                    Main.newLine(writer);
                } else if (this.mAdjacencyMatrix[j][i]) {
                    writer.write("\\path[p] (" + j + ") edge (" + i + ");");
                    Main.newLine(writer);
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
    public void printSCCs(final BufferedWriter writer, final boolean withSingletons, final boolean write) throws IOException {
        final int[] sccs = this.findSCCs(writer, write);
        int sccNum = -1;
        int checkedNodes = 0;
        int i = 0;
        boolean newScc = true;
        writer.write("Der gegebene Graph hat folgende starken Zusammenhangskomponenten:");
        Main.newLine(writer);
        Main.newLine(writer);
        writer.write("\\[");
        Main.newLine(writer);
        writer.write("    \\begin{array}{l}");
        Main.newLine(writer);
        while (checkedNodes < this.numOfAllNodes()) {
            if (sccs[i] == sccNum) {
                if (withSingletons || this.nodeHasAdjacentNodes(i)) {
                    if (newScc) {
                        writer.write("        \\{ " + this.nodeName(i));
                    } else {
                        writer.write(",\\ " + this.nodeName(i));
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
                    Main.newLine(writer);
                }
                i = 0;
                sccNum++;
                newScc = true;
            }
        }
        writer.write("    \\end{array}");
        Main.newLine(writer);
        writer.write("\\]");
        Main.newLine(writer);
        Main.newLine(writer);
    }

    /**
     * Prints the topological order of the graph of this exercise.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    public void printTopologicalOrder(
        final Optional<BufferedWriter> optionalExerciseWriter,
        final BufferedWriter solutionWriter,
        final boolean withSingletons,
        final boolean withText
    ) throws IOException {
        final Integer[] nodeValues = this.topologicSort();
        if (optionalExerciseWriter.isPresent()) {
            final BufferedWriter exerciseWriter = optionalExerciseWriter.get();
            if (withText) {
                exerciseWriter.write(
                              "Bestimmen Sie eine \\emphasize{topologische Sortierung} unter Verwendung des in " +
                            "der Vorlesung vorgestellten Algorithmus f\"ur den folgenden Graphen. Im gesamten " +
                            "Algorithmus werden Knoten in aufsteigender Reihenfolge ihrer Schl\\\"ussel ber\\\"ucksichtigt." +
                            " Als Ergebnis geben Sie die Liste der Knotenschl\\\"ussel in aufsteigender Reihenfolge der Topologiewerte an."
                              );
                Main.newLine(exerciseWriter);
            }
            this.printGraph(exerciseWriter, false);
        }
        solutionWriter.write("Der gegebene Graph hat die folgende topologische Sortierung:\\\\");
        Main.newLine(solutionWriter);
        Main.newLine(solutionWriter);
        int min = 1;
        int first = 0;
        for(int index = 0; index < nodeValues.length; ++index){
            if(nodeValues[index] != null && nodeValues[index] == min){
                solutionWriter.write(this.nodeName(index) + "("+ nodeValues[index] + ")");
                first = index;
                break;
            }
        }
        while(min <= nodeValues.length){
            for(int index = 0; index < nodeValues.length; ++index){
                if(nodeValues[index] != null && nodeValues[index] == min && first != index){
                    solutionWriter.write(", " + this.nodeName(index) + "("+ nodeValues[index] + ")");
                }
            }
            ++min;
        }
        solutionWriter.write("\\\\");
        Main.newLine(solutionWriter);
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
    void addEdges(final int from, final int to, final int type) {
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

    int dfsTopologicOrdering(final int start, final int[] colors, int topoNum, final Integer[] topo) throws IOException {
        colors[start] = 1;
        final int[] neighbors = this.getNeighbors(start);
        for(int index = 0; index < neighbors.length; ++index){
            //System.out.println("Start: " + (start+1) + ", Neighbor: " + (neighbors[index]+1));
            if(colors[neighbors[index]]==1){
                throw new IOException("The given graph is cyclic!");
            }
            if(colors[neighbors[index]] == 0){
                topoNum = this.dfsTopologicOrdering(neighbors[index], colors, topoNum, topo);
            }
        }
        topoNum +=1;
        topo[start] = topoNum;
        //System.out.println("Set topo of " + (start+1) + " to " + topoNum);
        colors[start] = 2;
        return topoNum;
    }

    void dfsWalk(final int from, int[] color, final List<Integer> result, final int ccNum, final int[] cc, final int directions, final int[] S, final int[] lastOfS) {
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

    int[] findSCCs(final BufferedWriter writer, final boolean write) throws IOException {
        final int[] result = new int[this.numOfAllNodes()];
        for (int w = 0; w < this.numOfAllNodes(); w++) {
            result[w] = -1;
        }

        // get the connected components
        final int[] color = new int[this.numOfAllNodes()];

        // split all connected components into strongly connected components
        final int[] lastOfS = new int[1]; // Array storing only one int, as it is not possible to call primitive types in java by reference..
        lastOfS[0] = -1;
        final int[] S = new int[this.numOfAllNodes()];
        final int[] colorA = new int[this.numOfAllNodes()];
        // phase 1
        if(write) {
            Main.newLine(writer);
            writer.write("Phase 1:");
            Main.newLine(writer);
            Main.newLine(writer);
            writer.write("\\medskip");
            Main.newLine(writer);
        }
        for (int w = 0; w < this.numOfAllNodes(); w++) {
            if (colorA[w] == 0) {
                this.dfsWalk(w, colorA, null, 0, null, 1, S, lastOfS);

                if(write && this.nodeHasAdjacentNodes(w)) {
                    this.printS(writer, S, lastOfS);
                    this.printColor(writer, colorA);
                    writer.write("\\medskip");
                    Main.newLine(writer);
                }
            }
        }
        // phase 2
        if(write) {
            writer.write("\\medskip");
            Main.newLine(writer);
            writer.write("Phase 2:");
            Main.newLine(writer);
            Main.newLine(writer);
            writer.write("\\medskip");
            Main.newLine(writer);
        }
        final int[] colorB = new int[this.numOfAllNodes()];
        while (lastOfS[0] > 0) {
            final Integer v = S[lastOfS[0]];
            lastOfS[0]--;
            //System.out.println("current element from stack: " + v + " with color " + colorB[v]);
            if (colorB[v] == 0) {
                final List<Integer> walk = new ArrayList<Integer>();
                this.dfsWalk(v, colorB, walk, 0, null, -1, null, null);
                for (final Integer node : walk) {
                    result[node] = v;
                }
                if(write && this.nodeHasAdjacentNodes(v)) {
                    this.printS(writer, S, lastOfS);
                    this.printColor(writer, colorB);
                    this.printScc(writer, result);
                    writer.write("\\medskip");
                    Main.newLine(writer);
                }
            }
        }
        return result;
    }

    int[] getNeighbors(final int nodeIndex){
        final ArrayList<Integer> result = new ArrayList<Integer>();
        for(int index = 0; index < this.mAdjacencyMatrix[nodeIndex].length; ++index){
            if(this.mAdjacencyMatrix[nodeIndex][index] == true){
                result.add(index);
            }
        }
        final int[] ret = new int[result.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = result.get(i).intValue();
        }
        return ret;
    }

    void printColor(final BufferedWriter writer, final int[] color) throws IOException {
        boolean firstWritten = false;
        writer.write("color: ");
        for (int i = 0; i < this.numOfAllNodes(); i++) {
            if (this.nodeHasAdjacentNodes(i)) {
                if (firstWritten) {
                    writer.write(", ");
                } else {
                    firstWritten = true;
                }
                writer.write("(" + this.nodeName(i) + ", ");
                if (color[i] == 0) {
                    writer.write("w)");
                } else if (color[i] == 1) {
                    writer.write("g)");
                } else {
                    writer.write("s)");
                }
            }
        }
        Main.newLine(writer);
        Main.newLine(writer);
    }

    void printS(final BufferedWriter writer, final int[] S, final int[] lastOfS) throws IOException {
        boolean firstWritten = false;
        writer.write("S: ");
        for (int i = 0; i <= lastOfS[0]; i++) {
            if (this.nodeHasAdjacentNodes(S[i])) {
                if (firstWritten) {
                    writer.write(", ");
                } else {
                    firstWritten = true;
                }
                writer.write("" + this.nodeName(S[i]));
            }
        }
        Main.newLine(writer);
        Main.newLine(writer);
    }

    void printScc(final BufferedWriter writer, final int[] scc) throws IOException {
        boolean firstWritten = false;
        writer.write("scc: ");
        for (int i = 0; i < this.numOfAllNodes(); i++) {
            if (this.nodeHasAdjacentNodes(i)) {
                if (firstWritten) {
                    writer.write(", ");
                } else {
                    firstWritten = true;
                }
                writer.write("(" + this.nodeName(i) + ", " + this.nodeName(scc[i]) + ")");
            }
        }
        Main.newLine(writer);
        Main.newLine(writer);
    }

    Integer[] topologicSort() throws IOException {
        final Integer[] result = new Integer[this.numOfAllNodes()];
        Arrays.fill(result, null);
        final int[] color = new int[this.numOfAllNodes()];
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

}
