package graph.implTest;

import graph.DirectLGraph;
import graph.Edge;
import org.junit.*;
import org.junit.rules.Timeout;

/**
 * TestGraphImpl provides implementation tests for the DirectLGraph class.
 * It tests remove methods and various special cases.
 */

public final class TestGraphImpl {
    private static DirectLGraph<String, String> testGraph = null;
    private static DirectLGraph<String, String> testGraph2 = null;

    @Rule   // 10 seconds max per method tested
    public Timeout globalTimeout = Timeout.seconds(10);

    @Before
    public void setup() {
        testGraph = new DirectLGraph<>();
        testGraph2 = new DirectLGraph<>();
    }

    @After
    public void reset() {
        testGraph = null;
        testGraph2 = null;
    }

    /**
     * Helper method: Fills the graph with a specified amount of nodes.
     *
     * @param graph     the DirectLGraph to generate nodes in.
     * @param numNodes  the number of nodes to generate in the graph.
     */
    private void createNodes(DirectLGraph<String, String> graph, int numNodes) {
        for (int i = 1; i <= numNodes; i++) {
            graph.addNode("n" + i);
        }
    }

    /**
     * Tests removing an existing edge from the graph.
     */
    @Test
    public void testRemoveEdge() {
        createNodes(testGraph, 2);

        testGraph.addEdge("n1", "n2", "edge1");
        Assert.assertEquals(1, testGraph.listChildren("n1").size());

        testGraph.removeEdge("n1", "n2", "edge1");
        Assert.assertEquals(testGraph.listChildren("n1").size(), 0);
    }

    /**
     * Tests creating edges with nodes that do not exist in the graph.
     */
    @Test
    public void testAddEdgeToNonExistentNodes() {
        createNodes(testGraph, 1);

        assert testGraph.hasNode("n1");
        assert !(testGraph.hasNode("a"));
        assert !(testGraph.hasNode("b"));

        testGraph.addEdge("n1", "a", "edge1");
        assert !(testGraph.hasEdge("n1", "a", "edge1"));
        testGraph.addEdge("n1", "b", "edge2");

        Assert.assertEquals(0, testGraph.listChildren("n1").size());
    }

    /**
     * Tests removing an edge that does not exist in the graph.
     */
    @Test
    public void testRemoveNonExistentEdge() {
        createNodes(testGraph, 2);

        testGraph.addEdge("n1", "n2", "edge1");
        Assert.assertEquals(1, testGraph.listChildren("n1").size());

        assert !(testGraph.hasEdge("a", "n2", "edge2"));
        boolean test1 = testGraph.removeEdge("a", "n2", "edge2");
        Assert.assertFalse(test1);

        assert !(testGraph.hasEdge("n2", "n1", "edge1"));
        boolean test2 = testGraph.removeEdge("n2", "n1", "edge2");
        Assert.assertFalse(test2);
    }

    /**
     * Tests adding the same node twice to the graph.
     */
    @Test
    public void testAddDuplicateNodes() {
        assert !(testGraph.hasNode("n1"));
        testGraph.addNode("n1");
        testGraph.addNode("n1");
        Assert.assertEquals(1, testGraph.getNodes().size());
    }

    /**
     * Tests adding the same edge twice to the graph.
     */
    @Test
    public void testAddDuplicateEdge() {
        createNodes(testGraph, 2);

        testGraph.addEdge("n1", "n2", "edge1");
        Assert.assertEquals(1, testGraph.listChildren("n1").size());

        testGraph.addEdge("n1", "n2", "edge1");
        Assert.assertEquals(1, testGraph.listChildren("n1").size());
    }

    /**
     * Tests removing an identical edge from two different graphs.
     */
    @Test
    public void testRemoveFromTwoGraphs() {
        createNodes(testGraph, 2);
        createNodes(testGraph2, 2);

        testGraph.addEdge("n1", "n2", "edge1");
        Assert.assertEquals(1, testGraph.listChildren("n1").size());
        Assert.assertEquals(0, testGraph2.listChildren("n1").size());

        testGraph2.addEdge("n1", "n2", "edge1");
        Assert.assertEquals(1, testGraph.listChildren("n1").size());
        Assert.assertEquals(1, testGraph2.listChildren("n1").size());

        testGraph.removeEdge("n1", "n2", "edge1");
        Assert.assertEquals(0, testGraph.listChildren("n1").size());
        Assert.assertEquals(1, testGraph2.listChildren("n1").size());

        testGraph2.removeEdge("n1", "n2", "edge1");
        Assert.assertEquals(0, testGraph.listChildren("n1").size());
        Assert.assertEquals(0, testGraph2.listChildren("n1").size());
    }

    /**
     * Tests toString method in DirectLGraph.
     */
    @Test
    public void testToString() {
        createNodes(testGraph, 4);

        testGraph.addEdge("n1", "n2", "edge1");
        testGraph.addEdge("n1", "n2", "edge2");
        testGraph.addEdge("n3", "n4", "edge3");

        // Test printing reflexive edge
        testGraph.addEdge("n1", "n1", "edge4");
        // Test printing parallel edge
        testGraph.addEdge("n1", "n2", "edge5");

        Assert.assertEquals("n1: (n1 --[edge2]--> n2) (n1 --[edge1]--> n2) " +
                        "(n1 --[edge4]--> n1) (n1 --[edge5]--> n2) " + "\n" + "n2: " +
                        "\n" + "n3: (n3 --[edge3]--> n4) " + "\n" + "n4: " + "\n",
                testGraph.toString());
    }

    /**
     * Tests equals method in DirectLGraph.
     */
    @Test
    public void testEquality() {
        createNodes(testGraph, 3);

        // Equivalent edges
        Edge<String, String> edge1 = new Edge<>("n1", "n2", "A");
        Edge<String, String> edge2 = new Edge<>("n1", "n2", "A");

        // Edge with different destination, same label
        Edge<String, String> edge3 = new Edge<>("n1", "n3", "A");

        // Edge with same source and destination, different label
        Edge<String, String> edge4 = new Edge<>("n1", "n2", "B");

        Assert.assertEquals(edge1, edge2);
        Assert.assertNotEquals(edge1,edge3);
        Assert.assertNotEquals(edge1, edge4);
    }
}