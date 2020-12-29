/*
* Copyright (C) 2020 Hal Perkins.  All rights reserved.  Permission is
* hereby granted to students registered for University of Washington
* CSE 331 for use solely during Winter Quarter 2020 for purposes of
* the course.  No other use, copying, distribution, or modification
* is permitted without prior written consent. Copyrights for
* third-party components of this work must be honored.  Instructors
* interested in reusing these course materials should contact the
* author.
*/

package pathfinder.specTest;

import graph.DirectLGraph;
import graph.Edge;
import pathfinder.ShortestPathFinder;
import pathfinder.datastructures.Path;

import java.io.*;
import java.util.*;

/**
* This class implements a test driver that uses a script file format
* to test an implementation of Dijkstra's algorithm on a graph.
*/
public class PathfinderTestDriver {

    /**
     * @spec.requires r != null && w != null
     * @spec.effects Creates a new MarvelTestDriver which reads command from
     * {@code r} and writes results to {@code w}
     **/
    public PathfinderTestDriver(Reader r, Writer w) {
        input = new BufferedReader(r);
        output = new PrintWriter(w);
    }

    /**
     * @throws IOException if the input or output sources encounter an IOException
     * @spec.effects Executes the commands read from the input and writes results to
     * the output
     **/
    public void runTests()
            throws IOException {
        String inputLine;
        while((inputLine = input.readLine()) != null) {
            if((inputLine.trim().length() == 0) ||
                    (inputLine.charAt(0) == '#')) {
                // echo blank and comment lines
                output.println(inputLine);
            } else {
                // separate the input line on white space
                StringTokenizer st = new StringTokenizer(inputLine);
                if(st.hasMoreTokens()) {
                    String command = st.nextToken();

                    List<String> arguments = new ArrayList<>();
                    while(st.hasMoreTokens()) {
                        arguments.add(st.nextToken());
                    }

                    executeCommand(command, arguments);
                }
            }
            output.flush();
        }
    }

    // *********************************
    // ***  Interactive Test Driver  ***
    // *********************************

    public static void main(String[] args) {
        try {
            if(args.length > 1) {
                printUsage();
                return;
            }

            PathfinderTestDriver td;

            if(args.length == 0) {
                td = new PathfinderTestDriver(new InputStreamReader(System.in), new OutputStreamWriter(System.out));
                System.out.println("Running in interactive mode.");
                System.out.println("Type a line in the spec testing language to see the output.");
            } else {
                String fileName = args[0];
                File tests = new File(fileName);

                System.out.println("Reading from the provided file.");
                System.out.println("Writing the output from running those tests to standard out.");
                if(tests.exists() || tests.canRead()) {
                    td = new PathfinderTestDriver(new FileReader(tests), new OutputStreamWriter(System.out));
                } else {
                    System.err.println("Cannot read from " + tests.toString());
                    printUsage();
                    return;
                }
            }

            td.runTests();

        } catch(IOException e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        }
    }

    private static void printUsage() {
        System.err.println("Usage:");
        System.err.println("  Run the gradle 'build' task");
        System.err.println("  Open a terminal at hw-pathfinder/build/classes/java/test");
        System.err.println("  To read from a file: java marvel.specTest.PathfinderTestDriver <name of input script>");
        System.err.println("  To read from standard in (interactive): java marvel.specTest.PathfinderTestDriver");
    }

    // ***************************
    // ***  JUnit Test Driver  ***
    // ***************************

    /**
     * String -> Graph: maps the names of graphs to the actual graph
     **/
    private final Map<String, DirectLGraph<String, Double>> graphs = new HashMap<>();
    private final PrintWriter output;
    private final BufferedReader input;

    private void executeCommand(String command, List<String> arguments) {
        try {
            switch(command) {
                case "CreateGraph":
                    createGraph(arguments);
                    break;
                case "AddNode":
                    addNode(arguments);
                    break;
                case "AddEdge":
                    addEdge(arguments);
                    break;
                case "ListNodes":
                    listNodes(arguments);
                    break;
                case "ListChildren":
                    listChildren(arguments);
                    break;
                case "FindPath":
                    findPath(arguments);
                    break;
                default:
                    output.println("Unrecognized command: " + command);
                    break;
            }
        } catch(Exception e) {
            output.println("Exception: " + e.toString());
        }
    }

    private void createGraph(List<String> arguments) {
        if(arguments.size() != 1) {
            throw new CommandException("Bad arguments to CreateGraph: " + arguments);
        }

        String graphName = arguments.get(0);
        createGraph(graphName);
    }

    private void createGraph(String graphName) {
        DirectLGraph<String, Double> graph1 = new DirectLGraph<>();
        graphs.put(graphName, graph1);

        output.println("created graph " + graphName);
    }

    private void addNode(List<String> arguments) {
        if(arguments.size() != 2) {
            throw new CommandException("Bad arguments to AddNode: " + arguments);
        }

        String graphName = arguments.get(0);
        String nodeName = arguments.get(1);

        addNode(graphName, nodeName);
    }

    private void addNode(String graphName, String nodeName) {
        DirectLGraph<String, Double> graph = graphs.get(graphName);
        graph.addNode(nodeName);

        output.println("added node " + nodeName + " to " + graphName);
    }

    private void addEdge(List<String> arguments) {
        if(arguments.size() != 4) {
            throw new CommandException("Bad arguments to AddEdge: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        String childName = arguments.get(2);
        double edgeLabel = Double.parseDouble(arguments.get(3));

        addEdge(graphName, parentName, childName, edgeLabel);
    }

    private void addEdge(String graphName, String parentName, String childName,
                         double edgeLabel) {

        DirectLGraph<String, Double> graph = graphs.get(graphName);
        graph.addEdge(parentName, childName, edgeLabel);

        output.println("added edge " + String.format("%.3f", edgeLabel) + " from " + parentName + " to " +
                childName + " in " + graphName);
    }

    private void listNodes(List<String> arguments) {
        if(arguments.size() != 1) {
            throw new CommandException("Bad arguments to ListNodes: " + arguments);
        }

        String graphName = arguments.get(0);
        listNodes(graphName);
    }

    private void listNodes(String graphName) {
        DirectLGraph<String, Double> graph = graphs.get(graphName);
        Set<String> nodes = new TreeSet<>(graph.getNodes());

        String result  = graphName + " contains:";
        if (!nodes.isEmpty()) {
            result += " " + (String.join(" ", nodes));
        }

        output.println(result.trim());
    }

    private void listChildren(List<String> arguments) {
        if(arguments.size() != 2) {
            throw new CommandException("Bad arguments to ListChildren: " + arguments);
        }

        String graphName = arguments.get(0);
        String parentName = arguments.get(1);
        listChildren(graphName, parentName);
    }

    private void listChildren(String graphName, String parentName) {
        DirectLGraph<String, Double> graph = graphs.get(graphName);

        Set<Edge<String, Double>> edges = new HashSet<>(graph.getEdges(parentName));
        Set<String> children = new TreeSet<>();

        for (Edge<String, Double> nextEdge : edges) {
            children.add((nextEdge.getDest()) + "(" + String.format("%.3f",
                    nextEdge.getLabel()) + ")");
        }

        String result = "the children of " + parentName + " in " + graphName + " are:";
        if (!children.isEmpty()) {
            result += " " + (String.join(" ", children));
        }

        output.println(result);
    }

    private void findPath(List<String> arguments) {
        if(arguments.size() != 3) {
            throw new CommandException("Bad arguments to findPath: " + arguments);
        }

        String graphName = arguments.get(0);
        String sourceName = arguments.get(1);
        String destName = arguments.get(2);

        findPath(graphName, sourceName, destName);
    }

    private void findPath(String graphName, String sourceName, String destName) {
        DirectLGraph<String, Double> graph = graphs.get(graphName);
        sourceName = sourceName.replace("_", " ");
        destName = destName.replace("_", " ");

        if (!graph.hasNode(sourceName) || !graph.hasNode(destName)) {
            if (!graph.hasNode(sourceName)) {
                output.println("unknown node " + sourceName);
            }
            if (!graph.hasNode(destName)) {
                output.println("unknown node " + destName);
            }

        } else {
            Path<String> path = ShortestPathFinder.
                    findShortestPath(graph, sourceName, destName);

            output.println("path from " + sourceName + " to " + destName + ":");

            if (path == null) {
                output.println("no path found");
            } else {
                String currentNode = sourceName;
                String result = "";

                for (Path<String>.Segment segment : path) {
                    String nextNode = segment.getEnd();
                    result += currentNode + " to " + nextNode + " with weight " +
                            String.format("%.3f", segment.getCost()) + "\n";

                    currentNode = nextNode;
                }

                if (!result.isEmpty()) {
                    output.println(result.trim());
                }
                output.println("total cost: " + String.format("%.3f",path.getCost()));
            }
        }
    }

    /**
     * This exception results when the input file cannot be parsed properly
     **/
    public static class CommandException extends RuntimeException {

        public CommandException() {
            super();
        }

        public CommandException(String s) {
            super(s);
        }

        public static final long serialVersionUID = 3495;
    }
}
