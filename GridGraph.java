import java.io.*;
import java.util.*;

/**
 * Programm for creating planar graphs with node positions according to a grid.
 * @author Florian Corzilius
 * @version $Id$
 */
public class GridGraph {
    /// The number of adjacent nodes to store in the sparse version of the adjacency matrix for the 
    /// nodes it considers. This sparse version is used to create the graph this exercise considers.
    private static int mNumOfNeighborsInSparseAdjacencyMatrix = 6;
    /// The number of rows in the grid according which the graph this exercise considers is ordered. (must be odd)
    private static int mNumOfRowsInGrid = 5;
    /// The number of columns in the grid according which the graph this exercise considers is ordered. (must be odd)
    private static int mNumOfColumnsInGrid = 7;
    /// The adjacency matrix of the graph used in this exercise.
    private boolean[][] mAdjacencyMatrix;
    
    /**
     * Creates an SCC exercise with the graph as given in the sparse version of the adjacency matrix.
     */
    public GridGraph() {
        int numOfNodes = mNumOfRowsInGrid * mNumOfColumnsInGrid;
        mAdjacencyMatrix = new boolean[numOfNodes][numOfNodes];
    }
    
    public int numOfNodesInSparseAdjacencyMatrix() {
        return (mNumOfRowsInGrid * mNumOfColumnsInGrid + 1) / 2;
    }
    
    public int numOfNeighborsInSparseAdjacencyMatrix() {
        return mNumOfNeighborsInSparseAdjacencyMatrix;
    }
    
    public int numOfRowsInGrid() {
        return mNumOfRowsInGrid;
    }
    
    public int numOfColumnsInGrid() {
        return mNumOfColumnsInGrid;
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
    
    /**
     * Creates the graph with node positioned according to a grid and the given sparse version of the adjacency matrix of
     * the graph to create.
     * @param sparseAdjacencyMatrix The sparse version of the adjacency matrix of the graph to create.
     */
    public void createGraph(int[][] sparseAdjacencyMatrix) {
        for (int i = 0; i < numOfNodesInSparseAdjacencyMatrix(); i++) {
            int nodeNum = 2*i;
            // north
            if (nodeNum > numOfColumnsInGrid()) {
                addEdges(nodeNum, nodeNum-numOfColumnsInGrid(), sparseAdjacencyMatrix[i][0]);
            }
            // east
            if (nodeNum % numOfColumnsInGrid() < numOfColumnsInGrid() - 1) {
                addEdges(nodeNum, nodeNum+1, sparseAdjacencyMatrix[i][1]);
            }
            // south
            if (nodeNum + numOfColumnsInGrid() < numOfAllNodes() - 1) {
                addEdges(nodeNum, nodeNum+numOfColumnsInGrid(), sparseAdjacencyMatrix[i][2]);
            }
            // south-west
            if (nodeNum + numOfColumnsInGrid() < numOfAllNodes() - 1 && nodeNum % numOfColumnsInGrid() > 0) {
                addEdges(nodeNum, nodeNum+numOfColumnsInGrid()-1, sparseAdjacencyMatrix[i][3]);
            }
            // west
            if (nodeNum % numOfColumnsInGrid() > 0) {
                addEdges(nodeNum, nodeNum-1, sparseAdjacencyMatrix[i][4]);
            }
            // north-west
            if (nodeNum > numOfColumnsInGrid() && nodeNum % numOfColumnsInGrid() > 0) {
                addEdges(nodeNum, nodeNum-numOfColumnsInGrid()-1, sparseAdjacencyMatrix[i][5]);
            }
        } 
    }
    
    void dfsWalk(int from, int[] color, List<Integer> result, int ccNum, int[] cc, int directions, Stack<Integer> blackColoringOrder) {
        if (color == null) {
            color = new int[numOfAllNodes()];
        }
        color[from] = 1;
        if (result != null) result.add(from);
        if (cc != null) cc[from] = ccNum;
        for (int i = 0; i < numOfAllNodes(); i++) {
            if (color[i] == 0 && ((directions >= 0 && mAdjacencyMatrix[from][i]) || (directions <= 0 && mAdjacencyMatrix[i][from]))) {
                dfsWalk(i, color, result, ccNum, cc, directions, blackColoringOrder);
            }
        }
        color[from] = 2;
        if (blackColoringOrder != null) blackColoringOrder.push(from);
    }
    
    int[] findSCCs() {
        int[] result = new int[numOfAllNodes()];
                
        // get the connected components
        int[] color = new int[numOfAllNodes()];
        int[] cc = new int[numOfAllNodes()];
        int ccNum = 0;
        for (int i = 0; i < numOfAllNodes(); i++) {
            if (color[i] == 0) {
                dfsWalk(i, color, null, ccNum++, cc, 0, null);
            }
        }
        //System.out.println("");
        //for (int i = 0; i < numOfAllNodes(); i++) {
        //    System.out.println("" + i + ": color(" + color[i] + "), cc(" + cc[i] + ")");
        //}
        
        // split all connected components into strongly connected components
        int sccNum = 0;
        Stack<Integer> blackColoringOrder = new Stack<Integer>();
        int[] colorA = new int[numOfAllNodes()];
        // phase 1
        while (blackColoringOrder.size() < numOfAllNodes()) {
            int w = 0;
            while (w < numOfAllNodes() && colorA[w] != 0) {
                w++;
            }
            if (w < numOfAllNodes()) {
                dfsWalk(w, colorA, null, 0, null, 1, blackColoringOrder);
            }
        }
        // phase 2
        int[] colorB = new int[numOfAllNodes()];
        while (!blackColoringOrder.empty()) {
            Integer v = blackColoringOrder.pop();
            //System.out.println("current element from stack: " + v + " with color " + colorB[v]);
            if (colorB[v] == 0) {
                List<Integer> walk = new ArrayList<Integer>();
                dfsWalk(v, colorB, walk, 0, null, -1, null);
                //System.out.println("");
                //for (int k = 0; k < numOfAllNodes(); k++) {
                //    System.out.println("" + k + ": colorB(" + colorB[k] + ")");
                //}
                //System.out.println("");
                //System.out.print("Found walk is: ");
                for (Integer node : walk) {
                 //   System.out.print(" " + node);
                    result[node] = sccNum;
                }
                //System.out.println("");
                sccNum++;
            }
        }
        return result;
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
                mAdjacencyMatrix[to][from] = true;
                mAdjacencyMatrix[from][to] = false;
                return;
            case 1:
                mAdjacencyMatrix[to][from] = false;
                mAdjacencyMatrix[from][to] = true;
                return;
            case 2:
                mAdjacencyMatrix[to][from] = true;
                mAdjacencyMatrix[from][to] = true;
                return;
            default:
                mAdjacencyMatrix[to][from] = false;
                mAdjacencyMatrix[from][to] = false;
                return;
        }
    }
    
    /**
     * Returns the number of all nodes, that includes the nodes which have no adjacent node and are considered as not existing in this exercise.
     */
    public int numOfAllNodes() {
        return mNumOfRowsInGrid * mNumOfColumnsInGrid;
    }
    
    public boolean nodeHasAdjacentNodes(int nodeNumber) {
        for(int i = 0; i < numOfAllNodes(); i++) {
            if (mAdjacencyMatrix[nodeNumber][i]) {
                return true;
            }
        }
        for(int i = 0; i < numOfAllNodes(); i++) {
            if (mAdjacencyMatrix[i][nodeNumber]) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Prints this AVL-tree right under the given headline.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printGraph(BufferedWriter writer, boolean withSingletons) throws IOException {
        printTikzBeginning(writer);
        // print the nodes
        writer.write("% The nodes:");
        writer.newLine();
        for (int i = 0; i < numOfAllNodes(); i++) {
            if (withSingletons || nodeHasAdjacentNodes(i)) {
                writer.write("\\node[node] (" + i + ") at (" + (i % numOfColumnsInGrid()) + "," + ((numOfAllNodes()-1-i) / numOfColumnsInGrid()) + ") {" + i + "};");
                writer.newLine();
            }
        }
        writer.write("% The edges:");
        writer.newLine();
        for (int i = 0; i < mAdjacencyMatrix.length; i++) {
            for (int j = i+1 ; j < mAdjacencyMatrix[i].length; j++) {
                if (mAdjacencyMatrix[i][j] && mAdjacencyMatrix[j][i]) {
                    writer.write("\\path[p, bend right=15] (" + i + ") edge (" + j + ");");
                    writer.newLine();
                    writer.write("\\path[p, bend right=15] (" + j + ") edge (" + i + ");");
                    writer.newLine();
                } else if (mAdjacencyMatrix[i][j]) {
                    writer.write("\\path[p] (" + i + ") edge (" + j + ");");
                    writer.newLine();
                } else if (mAdjacencyMatrix[j][i]) {
                    writer.write("\\path[p] (" + j + ") edge (" + i + ");");
                    writer.newLine();
                }
            }
        }
        // print the edges
        printTikzEnd(writer); 
    }
    
    /**
     * Prints all SCCs in the graph of this exercise.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printSCCs(BufferedWriter writer, boolean withSingletons) throws IOException {
        int[] sccs = findSCCs();
        int sccNum = 0;
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
        while (checkedNodes < numOfAllNodes()) {
            if (sccs[i] == sccNum) {
                if (withSingletons || nodeHasAdjacentNodes(i)) {
                    if (newScc) {
                        writer.write("        \\{ " + i);
                    } else {
                        writer.write(",\\ " + i);
                    }
                    newScc = false;
                }
                checkedNodes++;
            }
            i++;
            if (i == numOfAllNodes() || checkedNodes == numOfAllNodes()) {
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
     * Prints the beginning of the TikZ picture environment to the specified writer, including style settings for 
     * arrays or trees.
     * @param arrayStyle Flag indicating whether to use the array or tree style.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printTikzBeginning(BufferedWriter writer) throws IOException {
        writer.write("\\begin{center}");
        writer.newLine();
        writer.write("\\begin{tikzpicture}");
        writer.newLine();
        writer.write("[scale=2.4, node/.style={circle,draw=black,thin,inner sep=5pt}"
                     + ", >=stealth, p/.style={->, thin, shorten <=2pt, shorten >=2pt}]");
        writer.newLine();
    }

    /**
     * Prints the end of the TikZ picture environment to the specified writer.
     * @param writer The writer to send the output to.
     * @throws IOException If some error occurs during output.
     */
    private void printTikzEnd(BufferedWriter writer) throws IOException {
        writer.write("\\end{tikzpicture}");
        writer.newLine();
        writer.write("\\end{center}");
        writer.newLine();
    }
    
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
            writer.write(
                  "Geben Sie alle starken Zusammenhangskomponenten im folgenden Graph an. F\"ur jede dieser "
                + "starken Zusammenhangskomponenten reicht es die Menge der Knoten anzugeben, die darin auftreten."
            );
            writer.newLine();
            graph.printGraph(writer, false);
            if (writerSpace != null) {
                graph.printSCCs(writerSpace, false);
                writerSpace.newLine();
            }
        default:
            
        }
    }
}
