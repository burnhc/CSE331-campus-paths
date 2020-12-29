package graph;
import java.util.*;

/**
 * <b>DirectLGraph</b> is a mutable representation of a directed labeled
 * multigraph.
 *
 * This graph contains unique nodes connected by one-way edges. There can be
 * <i>any number</i> of edges between each pair of nodes (zero, one, or more).
 * Each edge has a <i>non-unique</i> label.
 *
 * No two edges with the same parent and child nodes will have the same edge
 * label, and no two nodes store equal data.
 *
 * @param <N> The type of nodes in the graph.
 * @param <E> The type of edges in the graph.
 *
 */
public class DirectLGraph<N,E> {

    // ABSTRACTION FUNCTION:
    //      DirectLGraph is a directed labeled multigraph that stores
    //      nodes and one-way labeled edges connected to each node.
    //
    //      AF(r) = Graph as a HashMap<N, Set<Edge<N,E>>> g such that
    //          "Graph is empty" = if g is empty.
    //          "n1: " = if g contains node n1 and n1 has no children.
    //          "n1: (n1 --[edge1]--> n2)" = if g contains node n1 with child
    //          node n2 connected one-way by edge labeled "edge1".

    // REPRESENTATION INVARIANT:
    //      All nodes and edges in the graph != null.
    //      No nodes store the same name.
    //      No two edges share the same source, destination, and label.


    // Debug flag; change to TRUE to enable checkReps.
    private static final boolean DEBUG = false;

    private final Map<N, Set<Edge<N,E>>> nodeMap;

    /**
     * Creates a new directed labeled multigraph.
     *
     * @spec.effects Constructs an empty DirectLGraph.
     */
    public DirectLGraph() {
        this.nodeMap = new HashMap<>();
        checkRep();
    }


    /**
     * Returns a set of all the nodes in this graph in
     * alphabetical order.
     *
     * @return A set of the nodes contained in this graph.
     */
    public Set<N> getNodes() {
        checkRep();
        return new HashSet<>(this.nodeMap.keySet());
    }

    /**
     * Returns a set of all the edges from the specified node.
     *
     * @param node The node to be queried.
     * @throws NullPointerException if node == null
     * @return A set of all the edges from this node, an empty set
     * if there are no edges from this node, or null if the specified
     * node does not exist in the graph.
     */
    public Set<Edge<N,E>> getEdges(N node) {
        checkRep();

        if (node == null || !hasNode(node)) {
            throw new NullPointerException("Node cannot be null.");
        }

        checkRep();
        return new HashSet<>(nodeMap.get(node));
    }

    /**
     * Returns whether this DirectLGraph contains the specified node.
     *
     * @param node The name of the node.
     * @spec.requires node != null
     * @return TRUE if this graph contains the node; FALSE if not.
     */
    public boolean hasNode(N node) {
        checkRep();
        return this.nodeMap.containsKey(node);
    }

    /**
     * Returns whether this graph contains the specified edge from
     * the specified node.
     *
     * @param source The starting node of the edge.
     * @param dest   The ending node of the edge.
     * @param label  The label of the edge to query.
     * @spec.requires source != null, dest != null, label != null
     * @return TRUE if this graph contains the edge; FALSE if not.
     */
    public boolean hasEdge(N source, N dest, E label) {
        checkRep();
        if (source == null || dest == null || label == null) {
            return false;
        }

        Edge<N,E> edgeToFind = new Edge<>(source, dest, label);
        Set<Edge<N,E>> edgeSet = this.nodeMap.get(source);

        if (edgeSet == null) {
            return false;
        }

        return edgeSet.contains(edgeToFind);
    }

    /**
     * Adds a node to this graph.
     *
     * @param node The name of the new node to be added.
     * @spec.effects Adds a node to the graph if the node does not already
     * exist.
     * @spec.requires node != null
     * @return TRUE if the new node is successfully added; FALSE if the node
     * already exists.
     */
    public boolean addNode(N node) {
        checkRep();
        if (hasNode(node)) {
            return false;
        }

        this.nodeMap.put(node, new HashSet<>());
        return true;
    }

    /**
     * Creates a labeled edge between two nodes.
     *
     * @param source The starting node of the edge.
     * @param dest   The ending node of the edge.
     * @param label  The label of the new edge to be added.
     * @spec.effects Adds a new edge to the graph if both of the nodes
     * exist and if the edge does not already exist.
     * @throws NullPointerException if any argument is null.
     * @return TRUE if the new edge is successfully added; FALSE if not
     * (the edge already exists or the source or destination nodes do not
     * exist.)
     */
    public boolean addEdge(N source, N dest, E label) {
        checkRep();
        if (source == null || dest == null || label == null) {
            throw new NullPointerException("Source, destination, or label" +
                    " cannot be null.");
        } else if (!hasNode(source) || !hasNode(dest)) {
            return false;
        }

        Edge<N,E> newEdge = new Edge<>(source, dest, label);
        return nodeMap.get(source).add(newEdge);
    }

    /**
     * Removes a specified edge between two nodes.
     *
     * @param source The starting node of the edge.
     * @param dest   The ending node of the edge.
     * @param label  The label of the edge to be removed.
     * @spec.effects Removes the edge from the graph if it exists.
     * @spec.requires source != null, dest != null, label != null
     * @return TRUE if the edge is successfully removed; FALSE if the edge
     * does not exist in the graph.
     */
    public boolean removeEdge(N source, N dest, E label) {
        checkRep();
        if (!hasEdge(source, dest, label)) {
            return false;
        }

        Edge<N,E> edgeToRemove = new Edge<>(source, dest, label);
        nodeMap.get(source).remove(edgeToRemove);
        return true;
    }

    /**
     * Returns a set of all the children of the specified node.
     *
     * @param node The node to be queried.
     * @spec.requires node != null
     * @return A set of all the children from this node, an empty set
     * if there are no children from this node, or null if the specified
     * node does not exist in the graph.
     */
    public Set<N> listChildren(N node) {
        checkRep();

        Set<Edge<N,E>> edges = new HashSet<>(getEdges(node));
        Set<N> children = new HashSet<>();

        for (Edge<N,E> nextEdge : edges) {
            children.add(nextEdge.getDest());
        }

        checkRep();
        return children;
    }

    /**
     * Returns a string representation of this graph, containing the
     * nodes and edges from each node.
     *
     * @return a string of the map of this graph or a blank line
     * if the map is empty.
     */
    @Override
    public String toString() {
        checkRep();
        if (nodeMap.isEmpty()) {
            return "{}";
        }

        StringBuilder result = new StringBuilder();

        for (N node : nodeMap.keySet()) {
            result.append(node).append(": ");

            for (Edge<N,E> edge : nodeMap.get(node)) {
                result.append(edge.toString()).append(" ");
            }
            result.append("\n");
        }

        return result.toString();
    }

    // Checks if the rep invariant holds. Throws exception if the
    // rep invariant is violated.

    private void checkRep() {
        if (DEBUG) {
            // Check for any null keys, edge sets, or edges.
            assert !(nodeMap.containsKey(null));
            assert !(nodeMap.containsValue(null));
            for (N node : nodeMap.keySet()) {
                assert !nodeMap.get(node).contains(null);
            }

            // Verify that no nodes store the same name data.
            Set<N> checkNodes = new HashSet<>(nodeMap.keySet());
            assert (checkNodes.size() == nodeMap.keySet().size());

            // Check for any identical edges.
            for (N node : checkNodes) {
                Set<Edge<N,E>> checkEdges = new HashSet<>(nodeMap.get(node));
                assert (checkEdges.size() == nodeMap.get(node).size());
            }
        }
    }
}