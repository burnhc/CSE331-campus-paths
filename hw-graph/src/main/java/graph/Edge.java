package graph;

/**
 * <b>Edge</b> is an immutable representation of a labeled edge from one node to
 * another in a directed labeled multigraph (DirectLGraph).
 *
 * @param <N> The type of nodes in the graph.
 * @param <E> The type of edges in the graph.
 */

public class Edge<N,E> {

    /** Stores the source, destination, and label of the edge. */
    public final N source;
    public final N dest;
    public final E label;

    // Debug flag; change to TRUE to enable checkReps.
    private static final boolean DEBUG = true;

    /**
     * Creates a new edge.
     *
     * @param source The starting node of the edge.
     * @param dest   The ending node of the edge.
     * @param label  The name of the new edge.
     * @spec.effects Constructs a new Edge starting from the specified
     * source node to the specified ending node.
     */
    public Edge(N source, N dest, E label) {
        this.source = source;
        this.dest = dest;
        this.label = label;
        checkRep();
    }

    /**
     * Returns the destination of this edge.
     *
     * @return the string destination of this edge.
     */
    public N getDest() {
        N dest = this.dest;
        return dest;
    }

    /**
     * Returns the label of this edge.
     *
     * @return the label of this edge.
     */
    public E getLabel() {
        E label = this.label;
        return label;
    }

    /**
     * Evaluates whether two edges have the same source and destination.
     *
     * @param obj The other object to be compared for equality.
     * @return TRUE if the edges have equivalent source and dest nodes and
     *         the same label; FALSE if not. If obj is null, return false.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Edge<?,?>)) {
            return false;
        }

        Edge<?,?> other = (Edge<?,?>) obj;
        return this.source.equals(other.source) && this.dest.equals(other.dest) &&
                this.label.equals(other.label);
    }

    /**
     * Returns a string representation of this edge.
     *
     * @return a string representing this edge in the form {@literal
     *         "(source --[label]--> dest)"}.
     */
    @Override
    public String toString() {
        return "(" + source + " --" + "[" + label + "]" + "--> " + dest + ")";
    }

    /**
     * Returns the hash code representing this edge.
     *
     * @return an integer hash code representing this edge.
     */
    @Override
    public int hashCode() {
        int code = (this.source.hashCode() + this.dest.hashCode() +
                this.label.hashCode());
        return code * 31;
    }

    // Checks if the rep invariant holds. Throws exception if the
    // rep invariant is violated.
    private void checkRep() {
        if (DEBUG) {
            assert source != null;
            assert dest != null;
            assert label != null;
        }
    }
}
