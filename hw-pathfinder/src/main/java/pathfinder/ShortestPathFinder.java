package pathfinder;

import graph.DirectLGraph;
import graph.Edge;
import pathfinder.datastructures.Path;

import java.util.*;

/**
 * <b>ShortestPathFinder</b> finds the lowest-cost path in
 * a directed labeled multigraph.
 *
 */
public class ShortestPathFinder {

    // This class does not represent an ADT.

    /**
     * Finds the lowest-cost path using Dijkstra's algorithm
     * between two nodes specified by the user. Dijkstra's
     * algorithm retrieves the path with the lowest cost
     * as determined by the sum of the numerical edge weights.
     *
     * @param graph The DirectLGraph to find the path from.
     * @param start The starting node that the path begins at.
     * @param dest  The destination node where the path ends.
     * @param <N>   The type of nodes in the graph.
     * @spec.requires Graph contains non-negative edge weights.
     * @return A new Path corresponding to the lowest-cost path
     * from the start to the destination. If no path exists,
     * return null.
     */
    public static <N> Path<N> findShortestPath
                            (DirectLGraph<N, Double> graph, N start, N dest) {

        // A path's “priority" in the queue is the total cost of that path.
        Comparator<Path<N>> comparePathCost = Comparator.comparing(Path::getCost);
        PriorityQueue<Path<N>> active = new PriorityQueue<>(comparePathCost);

        // Set of nodes for which we know the min-cost path from start
        Set<N> finished = new HashSet<>();

        //Add a path from start to itself to active
        active.add(new Path<>(start));

        while (!active.isEmpty()) {
            // minPath is the lowest-cost path in active and,
            // if minDest isn't already 'finished,' is the
            // minimum-cost path to the node minDest
            Path<N> minPath = active.poll();
            N minDest = minPath.getEnd();

            if (minDest.equals(dest)) {
                return minPath;
            }

            if (finished.contains(minDest)) {
                continue;
            }

            // For all children of minDest:
            Set<Edge<N, Double>> childrenEdges = graph.getEdges(minDest);
            for (Edge<N, Double> edge : childrenEdges) {

                // If we don't know the minimum-cost path from start to child,
                // examine the path we've just found
                if (!finished.contains(edge.getDest())) {

                    // newPath = minPath + e
                    Path<N> newPath = minPath.extend(edge.getDest(), edge.getLabel());

                    // add newPath to active
                    active.add(newPath);
                }

                // add minDest to finished
                finished.add(minDest);
            }
        }

        // No path exists from start to dest.
        return null;
    }
}
